package edu.pnu.config;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import edu.pnu.domain.Member;
import edu.pnu.persistence.MemberRepository;
import edu.pnu.websocket.CustomHandshakeInterceptor;

@Configuration
@EnableWebSocket // Boot WebSocket 활성화
public class AppWebSocketConfig extends TextWebSocketHandler implements WebSocketConfigurer {

	// 연결된 클라이언트들을 저정하는 Set
	private static Set<WebSocketSession> clients = Collections.synchronizedSet(new HashSet<WebSocketSession>());
	private static Map<WebSocketSession, Long> sessionToRegionMap = Collections.synchronizedMap(new HashMap<>());

	@Autowired
	private MemberRepository memberRepository;

	// WebSocket 연결명 설정 (http://localhost:8080/pushservice) ==> WebSocketConfigurer
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(this, "pushservice")
				.setAllowedOrigins("*")
				.addInterceptors(new CustomHandshakeInterceptor())
				.setHandshakeHandler(new DefaultHandshakeHandler());
	}

	// Client가 접속 시 호출되는 메서드
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        String query = session.getUri().getQuery();
//        clients.add(session);
//        if (query != null && query.startsWith("token=")) {
//            String token = query.replace("token=Bearer ", "");
//		session.close(CloseStatus.NOT_ACCEPTABLE);
//		return;
//	}
		List<String> protocols = session.getHandshakeHeaders().get("Sec-WebSocket-Protocol");

		if (protocols == null || protocols.isEmpty()) {
			session.close(CloseStatus.NOT_ACCEPTABLE);
			return;
		}

		String token = protocols.get(0).replace("Bearer ", "");
		String username = JWT.require(Algorithm.HMAC256("edu.pnu.jwt"))
				.build()
				.verify(token)
				.getClaim("username")
				.asString();

		Member member = memberRepository.findByMemberId(username).orElse(null);
		if (member != null) {
			clients.add(session);
			sessionToRegionMap.put(session, member.getRegion().getRegionId());
			System.out.println(session + " 클라이언트 접속: " + username);
			return;
		} else {
			System.out.println("찾을 수 없음");
			session.close(CloseStatus.NOT_ACCEPTABLE);
		}

	}

	// Client가 접속 해제 시 호출되는 메서드
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println(session + " 클라이언트 접속 해제");
		clients.remove(session);
		sessionToRegionMap.remove(session);
	}

	// FE에게 정보를 푸시하는 메소드
	public void sendPushMessage(Long regionId, String message) {
		if (clients.size() == 0) {
			System.out.println("No clients connected");
			return;
		}

		TextMessage textMessage = new TextMessage(message);

		synchronized (clients) {
			for (WebSocketSession sess : clients) {
				if (regionId.equals(sessionToRegionMap.get(sess))) {
					try {
						System.out.println("Sending message to session: " + sess.getId());
						sess.sendMessage(textMessage);
					} catch (IOException e) {
						System.out.println(sess.getRemoteAddress() + ":" + e.getMessage());
					}
				}
			}
		}
	}
}

package edu.pnu.config;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.pnu.domain.Alert;
import edu.pnu.domain.Member;
import edu.pnu.domain.Region;
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

	// Client가 접속 해제 시 호출되는 메서드
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println(session + " 클라이언트 접속 해제");
		clients.remove(session);
		sessionToRegionMap.remove(session);
	}
	
	// Client가 접속 시 호출되는 메서드
	@Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        clients.add(session);
        System.out.println("WebSocket connection established: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        // JSON 형식의 메시지를 파싱하여 토큰을 추출합니다.
        Map<String, String> msg = new ObjectMapper().readValue(payload, Map.class);
        if ("authenticate".equals(msg.get("type"))) {
            String token = msg.get("token").replace("Bearer ", "");
            String username = JWT.require(Algorithm.HMAC256("edu.pnu.jwt"))
                                 .build()
                                 .verify(token)
                                 .getClaim("username")
                                 .asString();

            Member member = memberRepository.findByMemberId(username).orElse(null);
            if (member != null) {
                sessionToRegionMap.put(session, member.getRegion().getRegionId());
                System.out.println(session + " 클라이언트 접속: " + username);
            } else {
                System.out.println("찾을 수 없음");
                session.close(CloseStatus.NOT_ACCEPTABLE);
            }
        }
    }
    
    // FE에게 정보를 푸시하는 메소드
    public void sendPushMessage(Long regionId, Alert alert) {
        if (clients.isEmpty()) {
            System.out.println("No clients connected");
            return;
        }
        
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage;

        try {
            jsonMessage = objectMapper.writeValueAsString(alert);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting Region object to JSON: " + e.getMessage());
            return;
        }

        TextMessage textMessage = new TextMessage(jsonMessage);

        synchronized (clients) {
            for (WebSocketSession sess : clients) {
                if (regionId.equals(sessionToRegionMap.get(sess))) {
                    try {
                        System.out.println("Sending message to session: " + sess.getId());
                        sess.sendMessage(textMessage);
                    } catch (IOException e) {
                        System.out.println(sess.getRemoteAddress() + ": " + e.getMessage());
                    }
                }
            }
        }
    }


}

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

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.pnu.domain.Member;
import edu.pnu.persistence.MemberRepository;

@Configuration
@EnableWebSocket	// Boot WebSocket 활성화
public class AppWebSocketConfig extends TextWebSocketHandler implements WebSocketConfigurer  {

	// 연결된 클라이언트들을 저정하는 Set
	private static Set<WebSocketSession> clients = Collections.synchronizedSet(new HashSet<WebSocketSession>());
	private static Map<WebSocketSession, Long> sessionToRegionMap = Collections.synchronizedMap(new HashMap<>());

    @Autowired
    private MemberRepository memberRepository;

	// WebSocket 연결명 설정 (http://localhost:8080/pushservice) ==> WebSocketConfigurer
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(this, "pushservice").setAllowedOrigins("*");
	}

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        clients.add(session);
        if (query != null && query.startsWith("token=")) {
            String token = query.replace("token=Bearer ", "");

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
            }else {
            	System.out.println("찾을 수 없음");
            }
        }
        session.close(CloseStatus.NOT_ACCEPTABLE);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println(session + " 클라이언트 접속 해제");
        clients.remove(session);
        sessionToRegionMap.remove(session);
    }			

//	// Client가 접속 시 호출되는 메서드
//		@Override
//		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//			clients.add(session);
//		    System.out.println(session + " 클라이언트 접속");
//		}
//		
//		// Client가 접속 해제 시 호출되는 메서드
//		@Override
//		public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//			System.out.println(session + " 클라이언트 접속 해제");
//			clients.remove(session);
//		}		
	// Client에서 메시지가 왔을 때 호출되는 메서드 ==> 채팅과 같은 형태의 기능을 추가하지 않는다면 필요없는 메소드이다.
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println("Message : " + message.getPayload());
	}
	
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
	// FE에게 정보를 푸시하는 메소드
//	public void sendPushMessage(PushDTO pushDto) {
//		// 연결된 클라이언트가 없으면 그냥 리턴
//	    if (clients.size() == 0)	return;
//
//	    // 자바 객체를 JSON 문자열로 변환
//	    ObjectMapper objectMapper = new ObjectMapper();
//	    String msg;
//		try {
//			msg = objectMapper.writeValueAsString(pushDto);
//		} catch (JsonProcessingException e) {
//			System.out.println("JSON Error:" + e.getMessage());
//			return;
//		}
//
//		// FE에 전송할 JSON 메시지객체 생성
//		TextMessage message = new TextMessage(msg);
//
//		// 블럭안에 코드를 수행하는 동안 map 객체에 대한 다른 스레드의 접근을 방지한다.
//		synchronized (clients) {
//		    for(WebSocketSession sess: clients) {
//		    	try {
//		    		sess.sendMessage(message);
//		    	} catch (IOException e) {
//		    		System.out.println(sess.getRemoteAddress() + ":" + e.getMessage());
//		    	}
//		    }
//		}
//	}
}

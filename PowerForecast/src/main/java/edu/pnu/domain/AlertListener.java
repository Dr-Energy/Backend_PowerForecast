package edu.pnu.domain;

import edu.pnu.config.AppWebSocketConfig;
import edu.pnu.config.SpringContext;
import jakarta.persistence.PostPersist;

//@Component
public class AlertListener {
	@PostPersist
	public void onPostPersist(Alert alert) {
		// AppWebSocketConfig 빈을 SpringContext에서 가져옵니다.
		AppWebSocketConfig webSocketConfig = SpringContext.getBean(AppWebSocketConfig.class);

		System.out.println("Alert added: " + alert.getRegion().getRegionId() + " - " + alert.getAlertType());

		if (webSocketConfig != null) {
			String message = "이상현상 발생: " + alert.getAlertType() + " in region " + alert.getRegion().getRegionId();
			System.out.println("Sending message: " + message);
			webSocketConfig.sendPushMessage(alert.getRegion().getRegionId(), message);
		} else {
			System.err.println("WebSocketConfig is null");
		}
	}
}

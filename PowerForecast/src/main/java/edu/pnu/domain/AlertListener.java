package edu.pnu.domain;

import java.text.SimpleDateFormat;

import edu.pnu.config.AppWebSocketConfig;
import edu.pnu.config.SpringContext;
import jakarta.persistence.PostPersist;

//@Component
public class AlertListener {
	@PostPersist
	public void onPostPersist(Alert alert) {
		// AppWebSocketConfig 빈을 SpringContext에서 가져옵니다.
		AppWebSocketConfig webSocketConfig = SpringContext.getBean(AppWebSocketConfig.class);
		System.out.println(alert.toString());

		String alertType = alert.getAlertType().equals(AlertType.ABNORMAL) ? "이상" 
						: alert.getAlertType().equals(AlertType.INCREASE) ? "상승" : "하락";
			
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 M월 dd일 a hh:mm");
		String msg = alert.getRegion().getSido() + " " + alert.getRegion().getGugun() + " " 
					+ alert.getRegion().getEupmyeondong() + " " + sdf.format(alert.getAlertTime())
					+ " 전력 수요 " + alertType + "이 예상됩니다.";
		System.out.println(msg);
		if (webSocketConfig != null) {
			System.out.println("Sending message: " + msg);
			webSocketConfig.sendPushMessage(alert.getRegion().getRegionId(), msg);
		} else {
			System.err.println("WebSocketConfig is null");
		}
	}
}

package edu.pnu.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import edu.pnu.service.PowerForecastService;

@RestController
public class PowerForecastController {

	@Autowired
	PowerForecastService powerForecastService;
	

    @GetMapping("/send-data")
    public ResponseEntity<String> sendData() {
        String url = "http://localhost:5000/receive-data";  // Flask 서버 URL

        // 데이터 생성
        Map<String, String> data = new HashMap<>();
        data.put("sido", "서울임");
        data.put("gugun", "Gangnam");
        data.put("eupmyeondong", "Apgujeong");

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 엔티티 생성
        HttpEntity<Map<String, String>> request = new HttpEntity<>(data, headers);

        // RestTemplate으로 POST 요청 전송
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        return response;
    }
}

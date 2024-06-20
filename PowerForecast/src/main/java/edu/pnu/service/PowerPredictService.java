package edu.pnu.service;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.pnu.DTO.Response.SkyItemDTO;
import edu.pnu.weather.util.WeatherApiUrlBuilder;

@Service
public class PowerPredictService {
	@Autowired
	private WeatherApiUrlBuilder weatherApiUrlBuilder;
	@Autowired
	private RestTemplate restTemplate;

	public String getPredict() throws Exception {
		String[] dates = weatherApiUrlBuilder.getDates();
		String baseDate = dates[1];
		System.out.println(baseDate);

		String baseTime = "2300";
		URI uri = weatherApiUrlBuilder.createAWSURI(baseDate, baseTime, "55", "127");
		System.out.println(uri);

		ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
	    
		String jsonRes = response.getBody();
		// 응답 본문 확인
	    if (jsonRes == null) {
	        System.out.println("Response body is null.");
	        return null;
	    }

	    System.out.println("Response body: " + jsonRes);
	    
		// ObjectMapper로 JSON 문자열을 객체로 받아오기
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode root = objectMapper.readTree(jsonRes);
		JsonNode itemsNode = root.path("response").path("body").path("items").path("item");
		
		List<SkyItemDTO> items = objectMapper.readValue(itemsNode.toString(), new TypeReference<List<SkyItemDTO>>() {});
		for(SkyItemDTO item : items) {			
			System.out.println(item);
		}
		
		System.out.println("[응답 완료]");
		return null;
	}
//	
//	public String getPredict() {
//		String url = "http://10.125.121.218:5000/receive-data"; // Flask 서버 URL
//
//		System.out.println("[데이터 전송]");
//		// 데이터 생성
//		Map<String, String> data = new HashMap<>();
//		data.put("sido", "서울임");
//		data.put("gugun", "Gangnam");
//		data.put("eupmyeondong", "Apgujeong");
//
//		// 헤더 설정
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//
//		// 요청 엔티티 생성
//		HttpEntity<Map<String, String>> request = new HttpEntity<>(data, headers);
//
//		// RestTemplate으로 POST 요청 전송
//		RestTemplate restTemplate = new RestTemplate();
//		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
//
//		return response.getBody();
//	}
}

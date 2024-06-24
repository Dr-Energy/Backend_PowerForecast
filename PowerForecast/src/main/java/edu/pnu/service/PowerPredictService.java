package edu.pnu.service;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.pnu.DTO.PowerPredictDTO;
import edu.pnu.DTO.WeatherDTO;
import edu.pnu.DTO.Response.SkyItemDTO;
import edu.pnu.domain.PowerPrediction;
import edu.pnu.persistence.PowerPredictionRepository;
import edu.pnu.persistence.RegionRepository;
import edu.pnu.weather.util.WeatherApiUrlBuilder;

@Service
public class PowerPredictService {
	@Autowired
	private WeatherApiUrlBuilder weatherApiUrlBuilder;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private PowerPredictionRepository powerPreRepository;
	
	public List<PowerPredictDTO> getPredict() {
		List<PowerPrediction> preList = powerPreRepository.findAllByRequestSeq(1L);
		
		List<PowerPredictDTO> preDTOList = new ArrayList<>();
		for(PowerPrediction power:preList) {
			System.out.println(power.getPredictTime());
			PowerPredictDTO dto = PowerPredictDTO.builder()
					.power(power.getPower())
					.time(power.getPredictTime())
					.build();
			preDTOList.add(dto);
		}
		return preDTOList;
	}
//	public String getWeatherInfo() throws Exception {
		
//		String lon = regionInfo.getLongitude();
//		String lat =  regionInfo.getLatitude();
//
//		String[] dates = getDates();
//		String curDate = dates[0];
//		String baseDate = dates[1];
//
//		String baseTime = "2300";
//		String curTime = getCurrentTimeAdjusted();
//		String itv = "1440";
//		String obs = "ta,hm,rn_60m,ta_chi";
//
//		RestTemplate restTemplate = new RestTemplate();
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(new MediaType("application", "JSON", Charset.forName("UTF-8")));
//		URI uri = createAWSURI(baseDate, baseTime, nx, ny);
//		URI uri2 = createWeatherURI(baseDate, curDate, curTime, obs, itv, lon, lat);
//
//		ResponseEntity<String> response1 = restTemplate.getForEntity(uri, String.class);
//		ResponseEntity<String> response2 = restTemplate.getForEntity(uri2, String.class);
//
//		String jsonRes1 = response1.getBody();
//		String jsonRes2 = response2.getBody();
//
//		// ObjectMapper로 JSON 문자열을 객체로 받아오기
//		ObjectMapper objectMapper = new ObjectMapper();
//		JsonNode root = objectMapper.readTree(jsonRes1);
//		JsonNode itemsNode = root.path("response").path("body").path("items").path("item");
//		List<SkyItemDTO> items = objectMapper.readValue(itemsNode.toString(), new TypeReference<List<SkyItemDTO>>() {});
//
//		WeatherDTO weather = filterWeatherData(items, curDate, curTime);
//		List<WeatherDTO> listWeather = parseWeatherData(jsonRes2, weather);
//		System.out.println("[응답 완료]");
//		return listWeather;
//		return null;
//	}

	
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

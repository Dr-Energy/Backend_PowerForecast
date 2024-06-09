package edu.pnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import edu.pnu.domain.Region;

@Service
public class WeatherService {
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${basic-api-key}")
	private String basic_api_key;
	
	private String nx;
	private String ny;
	
	 public String getWeatherData(Region region) {
		
        String url = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=" + basic_api_key
        			+ "&pageNo=1&numOfRows=1000&dataType=JSON&base_date=20240609&base_time=1700&nx=" + nx
        			+ "&ny=" + ny;
        return restTemplate.getForObject(url, String.class);
	 }
}

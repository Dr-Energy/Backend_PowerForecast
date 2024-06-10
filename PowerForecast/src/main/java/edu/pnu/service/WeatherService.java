package edu.pnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import edu.pnu.domain.Region;
import edu.pnu.persistence.RegionRepository;

@Service
public class WeatherService {
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RegionRepository regionRepository;
	@Value("${basic-api-key}")
	private String basic_api_key;
	@Value("${decode-api-key}")
	private String decode_api_key;
	
	private String nx;
	private String ny;
	
	 public String getWeatherData(String sido, String gugun, String eupmyeondong) {
		Region regionInfo = regionRepository.findBySidoAndGugunAndEupmyeondong(sido, gugun, eupmyeondong).get();
		nx = regionInfo.getGridX();
		ny = regionInfo.getGridY();
		System.out.println(decode_api_key);
//        String url = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=" + basic_api_key
//        			+ "&pageNo=1&numOfRows=1000&dataType=JSON&base_date=20240609&base_time=1700&nx=" + nx
//        			+ "&ny=" + ny;
		String url = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=wlWGq5R0p7W%2B4eib5opqyqWDaIFIfE%2BoRVSPx%2FBOq1G43JSfe2DY2wShbIqcrIY45gcNxwqFAEO9sxh89%2FQhrQ%3D%3D&pageNo=1&numOfRows=1000&dataType=json&base_date=20240610&base_time=0500&nx=55&ny=127";
        
        System.out.println(url);
        try {
//            return restTemplate.getForObject(url, String.class);
            System.out.println(restTemplate.getForObject(url, String.class));
            return "ok";
        } catch (HttpClientErrorException e) {
            return "HTTP Error: " + e.getStatusCode();
        } catch (RestClientException e) {
            return "Client Error: " + e.getMessage();
        }
	 }
}

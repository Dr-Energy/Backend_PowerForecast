package edu.pnu.controller;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.pnu.service.WeatherService;

@RestController
public class WeatherController {
	@Autowired
	private WeatherService weatherService;
	
	@GetMapping("/main/weather")
	public ResponseEntity<?> getWeather(@RequestParam String sido,
            			   @RequestParam String gugun,
            			   @RequestParam String eupmyeondong) throws UnsupportedEncodingException, URISyntaxException, JsonMappingException, JsonProcessingException {
//		System.out.println(sido+","+gugun+","+eupmyeondong);
		try {
			return weatherService.getWeatherData(sido, gugun, eupmyeondong);            
        } catch (RestClientException e) {
        	return ResponseEntity.badRequest().body(e.getMessage());
        }
	}
	
}

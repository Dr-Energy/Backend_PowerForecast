package edu.pnu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.service.WeatherService;

@RestController
public class WeatherController {
	@Autowired
	private WeatherService weatherService;
	
	@GetMapping("/main/weather")
	public ResponseEntity<?> getWeather( @RequestParam(required = false) String sido,
	        							 @RequestParam(required = false) String gugun,
	        							 @RequestParam(required = false) String eupmyeondong) throws Exception {
		System.out.println("[날씨 요청]");
		try {
			 // 파라미터가 없을 때의 기본 동작을 정의하거나, 기본 값을 설정할 수 있습니다.
	        if (sido == null || gugun == null || eupmyeondong == null) {
	            // 기본 값을 설정하거나, 파라미터가 없을 때의 동작을 정의합니다.
	            sido = (sido == null) ? "서울특별시" : sido;
	            gugun = (gugun == null) ? "종로구" : gugun;
	            eupmyeondong = (eupmyeondong == null) ? "청운효자동" : eupmyeondong;
	        }
	        
			return ResponseEntity.ok(weatherService.getWeatherData(sido, gugun, eupmyeondong));            
        } catch (Exception e) {
        	return ResponseEntity.badRequest().body(e.getMessage());
        } 
	}
	
	@GetMapping("/main/weather/{regionId}")
	public ResponseEntity<?> getWeather(@PathVariable Long regionId) throws Exception{
		System.out.println("[날씨 요청]");
		try {
			return ResponseEntity.ok(weatherService.getWeatherData(regionId));            
        } catch (Exception e) {
        	return ResponseEntity.badRequest().body(e.getMessage());
        }
	}
	
}

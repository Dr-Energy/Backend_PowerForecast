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
	public ResponseEntity<?> getWeather(@RequestParam String sido,
            			   @RequestParam String gugun,
            			   @RequestParam String eupmyeondong) throws Exception {
		try {
			return ResponseEntity.ok(weatherService.getWeatherData(sido, gugun, eupmyeondong));            
        } catch (Exception e) {
        	return ResponseEntity.badRequest().body(e.getMessage());
        } 
	}
	
	@GetMapping("/main/weather/{regionId}")
	public ResponseEntity<?> getWeather(@PathVariable Long regionId) throws Exception{
		try {
			return ResponseEntity.ok(weatherService.getWeatherData(regionId));            
        } catch (Exception e) {
        	return ResponseEntity.badRequest().body(e.getMessage());
        }
	}
	
}

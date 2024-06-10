package edu.pnu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import edu.pnu.service.WeatherService;

@RestController
public class WeatherController {
	@Autowired
	private WeatherService weatherService;
	
	@GetMapping("/main/weather")
	public void getWeather(@RequestParam String sido,
            			   @RequestParam String gugun,
            			   @RequestParam String eupmyeondong) {
		System.out.println(sido+","+gugun+","+eupmyeondong);
		try {
			System.out.println(weatherService.getWeatherData(sido, gugun, eupmyeondong));            
        } catch (RestClientException e) {
            System.out.println("Error fetching weather data: " + e.getMessage());
        }
	}
	
}

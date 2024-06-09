package edu.pnu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.Region;
import edu.pnu.service.WeatherService;

@RestController
public class WeatherController {
	@Autowired
	private WeatherService weatherService;
	
	@GetMapping("/main/weather/{region}")
	public void getWeather(@PathVariable Region region) {
		System.out.println(region);
		System.out.println(weatherService.getWeatherData(region));
	}
	
}

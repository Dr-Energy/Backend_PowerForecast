package edu.pnu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.pnu.DTO.WeatherDTO;
import edu.pnu.domain.Region;
import edu.pnu.persistence.RegionRepository;
import edu.pnu.weather.util.WeatherApiUrlBuilder;

@Service
public class WeatherService {
	
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private WeatherApiUrlBuilder weatherApiUrlBuilder;

	public List<WeatherDTO> getWeatherData(String sido, String gugun, String eupmyeondong) throws Exception {
		Region regionInfo = regionRepository.findBySidoAndGugunAndEupmyeondong(sido, gugun, eupmyeondong).get();
		return weatherApiUrlBuilder.getAPIData(regionInfo);
	}

	public List<WeatherDTO> getWeatherData(Long regionId) throws Exception {
		Region regionInfo = regionRepository.findById(regionId).get();
		return weatherApiUrlBuilder.getAPIData(regionInfo);
	}
}

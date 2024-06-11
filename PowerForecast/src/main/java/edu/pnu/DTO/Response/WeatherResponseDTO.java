package edu.pnu.DTO.Response;

import lombok.Data;

@Data
public class WeatherResponseDTO {
	private WeatherHeaderDTO header;
    private WeatherBodyDTO body;
}

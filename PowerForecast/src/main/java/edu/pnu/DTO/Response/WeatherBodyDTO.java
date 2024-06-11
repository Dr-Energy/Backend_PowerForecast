package edu.pnu.DTO.Response;

import lombok.Data;

@Data
public class WeatherBodyDTO {
	private String dataType;
	private WeatherItemsDTO items;
}

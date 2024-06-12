package edu.pnu.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeatherDTO {
	private String category;
	private String value;
	private String temp;
	private String humidity;
	private String rain;
	private String bodyTemp;
	
}

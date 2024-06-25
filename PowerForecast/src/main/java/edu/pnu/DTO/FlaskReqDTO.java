package edu.pnu.DTO;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import edu.pnu.domain.Weather;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FlaskReqDTO {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date timestamp;
    private String hh24;
    private String weekName;
    private String temp;
    private String bodyTemp;
    
    public static FlaskReqDTO convertToDTO(Weather weather) {
    	FlaskReqDTO weatherDTO = new FlaskReqDTO();
        weatherDTO.setTimestamp(weather.getTimestamp());
        weatherDTO.setHh24(weather.getHh24());
        weatherDTO.setWeekName(weather.getWeekName());
        weatherDTO.setTemp(weather.getTemp());
        weatherDTO.setBodyTemp(weather.getBodyTemp());
        return weatherDTO;
    }
}

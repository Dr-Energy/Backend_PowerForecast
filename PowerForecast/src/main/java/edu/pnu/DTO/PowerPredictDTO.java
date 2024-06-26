package edu.pnu.DTO;

import java.util.Date;

import edu.pnu.domain.PowerPrediction;
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
public class PowerPredictDTO {
	private Date time;
	private Float power;
	
	public static PowerPredictDTO convertToDTO(PowerPrediction power) {
    	PowerPredictDTO dto = new PowerPredictDTO();
        dto.setPower(power.getPower());
        dto.setTime(power.getPredictTime());
        return dto;
    }
	
	public static PowerPredictDTO convertToDTO(Weather power) {
    	PowerPredictDTO dto = new PowerPredictDTO();
    	float electFloat = Float.parseFloat(power.getElectPower());
        dto.setPower(electFloat);
        dto.setTime(power.getTimestamp());
        return dto;
    }
}
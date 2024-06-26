package edu.pnu.DTO;

import java.util.Date;

import edu.pnu.domain.PowerPrediction;
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
}
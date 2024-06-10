package edu.pnu.DTO;

import java.util.Date;

import edu.pnu.domain.Region;
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
public class RegionDTO {
	private String sido;
	private String gugun;
	private String eupmyeondong;
}

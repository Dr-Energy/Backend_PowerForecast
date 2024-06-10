package edu.pnu.DTO;

import java.util.Date;

import edu.pnu.domain.Region;
import edu.pnu.domain.Role;
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
public class AlertDTO {
	private RegionDTO region;
	private Date alertTime;
}

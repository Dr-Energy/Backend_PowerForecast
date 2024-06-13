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
public class MyPageDTO {
	private String nickname;
	private String memberId;
	private String phoneNumber;
	private RegionDTO region;
}

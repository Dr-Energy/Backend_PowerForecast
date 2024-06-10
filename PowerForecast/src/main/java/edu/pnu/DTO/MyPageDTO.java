package edu.pnu.DTO;

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
public class MyPageDTO {
	private String nickname;
	private String memberId;
	private String phoneNumber;
	private Region region;
//	private String region;
}

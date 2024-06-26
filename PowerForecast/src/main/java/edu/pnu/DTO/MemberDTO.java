package edu.pnu.DTO;

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
public class MemberDTO {
	private String nickname;
	private Long regionId;
	private Role role;
	private String memberId;
}

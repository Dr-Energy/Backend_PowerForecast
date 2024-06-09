package edu.pnu;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.pnu.domain.Member;
import edu.pnu.domain.Region;
import edu.pnu.domain.Role;
import edu.pnu.persistence.MemberRepository;
import edu.pnu.persistence.RegionRepository;

@SpringBootTest
public class MemberTest {
	@Autowired
	MemberRepository memberRepo;
	@Autowired
	RegionRepository regionRepo;
	
//	@Test
	public void addMemberList() {
		Region region = regionRepo.findById(1L).get();
		
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		
		Member mem = Member.builder()
				.memberId("scott")
				.password(encoder.encode("abcd"))
				.nickname("scott")
				.phoneNumber("01012345678")
				.role(Role.ROLE_USER)
				.region(region)
				.build();
		
		memberRepo.save(mem);
		
		region = regionRepo.findById(3560L).get();
		Member mem2 = Member.builder()
				.memberId("admin")
				.password(encoder.encode("abcd"))
				.nickname("LYI")
				.phoneNumber("01011111111")
				.role(Role.ROLE_ADMIN)
				.region(region)
				.build();
		
		memberRepo.save(mem2);
	}
	
	@Test
	public void getMemberList() {
		List<Member> members = memberRepo.findAll();
		
		for(int i=0; i<members.size(); i++) {
			System.out.println(members.get(i));
		}
	}
	
//	@Test
	public void getMemberByMebmerId() {
		Member member = memberRepo.findByMemberId("scott").get();
		
		System.out.println(member);
	}
}

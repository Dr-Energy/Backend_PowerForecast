package edu.pnu;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
		
		Member mem = Member.builder()
				.id("scott")
				.password("abcd")
				.nickname("scott")
				.phoneNumber("01012345678")
				.role(Role.USER)
				.region(region)
				.build();
		
		memberRepo.save(mem);
	}
	
	@Test
	public void getMemberList() {
		List<Member> members = memberRepo.findAll();
		
		for(int i=0; i<members.size(); i++) {
			System.out.println(members.get(i));
		}
	}
}

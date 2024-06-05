package edu.pnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.pnu.domain.Member;
import edu.pnu.domain.Region;
import edu.pnu.persistence.MemberRepository;
import edu.pnu.persistence.RegionRepository;

@Service
public class MemberService {
	@Autowired
	RegionRepository regionRepo;
	@Autowired
	MemberRepository memberRepo;
	
	PasswordEncoder encoder = new BCryptPasswordEncoder();
	
	public String addMember(Member member) {
		Region region = member.getRegion();
		region = regionRepo.findBySidoAndGugunAndEupmyeondong(region.getSido()+"11", region.getGugun(), region.getEupmyeondong())
						.orElse(null);
		
		if(region == null)
			return "잘못된 입력입니다";
		else {
			member.setPassword(encoder.encode(member.getPassword()));
			member.setRegion(region);
			memberRepo.save(member);
			return "등록되었습니다";
		}
		
	}
}

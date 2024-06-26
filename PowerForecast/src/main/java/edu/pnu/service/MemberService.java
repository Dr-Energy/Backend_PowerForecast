package edu.pnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.pnu.DTO.MemberDTO;
import edu.pnu.DTO.MyPageDTO;
import edu.pnu.DTO.RegionDTO;
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
		region = regionRepo
				.findBySidoAndGugunAndEupmyeondong(region.getSido(), region.getGugun(), region.getEupmyeondong())
				.orElse(null);

		if (region == null)
			return "잘못된 입력입니다";
		else {
			member.setPassword(encoder.encode(member.getPassword()));
			member.setRegion(region);
			memberRepo.save(member);
			return "등록되었습니다";
		}
	}

	public boolean isMemberIdAvailable(String memberId) {
		Member mem = memberRepo.existsByMemberId(memberId);
		if(mem == null)
			return true;
		else
			return false;
	}

	public String findId(Member member) {
		Member mem = memberRepo.findByPhoneNumber(member.getPhoneNumber());

		if (mem == null)
			return "찾을 수 없는 회원입니다";
		else {
			return mem.getMemberId();
		}
	}

	public String findPassword(Member member) {
		Member mem = memberRepo.findByMemberIdAndPhoneNumber(member.getMemberId(), member.getPhoneNumber());

		if (mem == null)
			return null;
		else {
			mem.setPassword(encoder.encode("abcd"));
			memberRepo.save(mem);
			return "abcd";
		}
	}

	public MyPageDTO getMemberInfo(String memberId) {
		Member mem = memberRepo.findByMemberId(memberId).orElse(null);

		if (mem == null)
			return null;
		else {
			RegionDTO regionDTO = RegionDTO.builder().sido(mem.getRegion().getSido()).gugun(mem.getRegion().getGugun())
					.eupmyeondong(mem.getRegion().getEupmyeondong()).build();

			MyPageDTO mypageDTO = MyPageDTO.builder().nickname(mem.getNickname()).memberId(mem.getMemberId())
					.phoneNumber(mem.getPhoneNumber()).region(regionDTO).build();
			return mypageDTO;
		}

	}

	public MemberDTO updateMemberInfo(Member member) {
		Member mem = memberRepo.findByMemberId(member.getMemberId()).orElse(null);
		Region prevRegion = member.getRegion();
		Region region = regionRepo.findBySidoAndGugunAndEupmyeondong(prevRegion.getSido(), prevRegion.getGugun(),
				prevRegion.getEupmyeondong()).orElse(null);

		if (mem == null || region == null)
			return null;
		else {
			mem.setNickname(member.getNickname());
			mem.setPhoneNumber(member.getPhoneNumber());
			mem.setRegion(region);
			mem.setPassword(encoder.encode(member.getPassword()));
			memberRepo.save(mem);
			
			MemberDTO memberDTO = MemberDTO.builder()
						.nickname(mem.getNickname())
						.regionId(mem.getRegion().getRegionId())
						.role(mem.getRole())
						.memberId(mem.getMemberId())
						.build();
						
			return memberDTO;
		}
	}

	public Member deleteMember(String memberId) {
		Member mem = memberRepo.findByMemberId(memberId).orElse(null);

		if (mem == null)
			return null;
		else {
			memberRepo.delete(mem);
			return mem;
		}
	}

//	public MemberDTO getMemberInfo(String id) {
//		Member mem = memberRepo.findByMemberId(id).get();
//		MemberDTO dto = MemberDTO.builder()
//				.nickname(mem.getNickname())
//				.region(mem.getRegion())
//				.build();
//		return dto;
//	}
}

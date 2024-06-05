package edu.pnu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.Member;
import edu.pnu.service.MemberService;

@RestController
public class MemberController {
	@Autowired
	MemberService memberService;
	
	@PostMapping("user/register")
	public ResponseEntity<?> addMember(@RequestBody Member member){
		String result = memberService.addMember(member);
		
		if(result.equals("잘못된 입력입니다"))
			return ResponseEntity.badRequest().body(result);
		else
			return ResponseEntity.ok(result);
	}
	
}

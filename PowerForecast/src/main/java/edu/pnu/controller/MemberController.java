package edu.pnu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.domain.Member;
import edu.pnu.service.MemberService;

@RestController
public class MemberController {
	@Autowired
	MemberService memberService;
	
	@PostMapping("/user/register")
	public ResponseEntity<?> addMember(@RequestBody Member member){
		String result = memberService.addMember(member);
		
		if(result.equals("잘못된 입력입니다"))
			return ResponseEntity.badRequest().body(result);
		else
			return ResponseEntity.ok(result);
	}
	
	@PostMapping("/user/find/id")
	public ResponseEntity<?> findId(@RequestBody Member member){
		String result = memberService.findId(member);
		
		if(result.equals("잘못된 입력입니다"))
			return ResponseEntity.badRequest().body(result);
		else
			return ResponseEntity.ok(result);
	}
	
	@PostMapping("/user/find/password")
	public ResponseEntity<?> findPassword(@RequestBody Member member){
		String result = memberService.findPassword(member);
		
		if(result.equals("잘못된 입력입니다"))
			return ResponseEntity.badRequest().body(result);
		else
			return ResponseEntity.ok(result);
	}
	
	// 로그인세션정보확인용URL
	/*
	 * @GetMapping("/auth") public @ResponseBody ResponseEntity<?>
	 * auth(@AuthenticationPrincipal User user){ if(user == null) { return
	 * ResponseEntity.ok("로그인 상태가 아닙니다."); }else {
	 * 
	 * return ResponseEntity.ok(user); } }
	 */

}

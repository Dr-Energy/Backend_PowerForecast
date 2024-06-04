package edu.pnu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {
	@GetMapping("/main")
	public String index() {
		return "main";
	}
	
	@GetMapping("/user/profile")
	public String member() {
		return "profile";
	}
}
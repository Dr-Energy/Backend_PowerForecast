package edu.pnu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import edu.pnu.DTO.AlertDTO;
import edu.pnu.service.AlertService;
import edu.pnu.service.MemberService;

@RestController
public class AlertController {
	@Autowired
	AlertService alertService;
	
	@GetMapping("/history/all")
	public ResponseEntity<?> getAlertList(){
		List<AlertDTO> alertList = alertService.getAlertList();
		
		return ResponseEntity.ok(alertList);
	}
	
	@GetMapping("/history/{regionId}")
	public ResponseEntity<?> getAlertRegionList(@PathVariable Long regionId){
		List<AlertDTO> alertList = alertService.getAlertRegionList(regionId);
		
		return ResponseEntity.ok(alertList);
	}
	
	@GetMapping("/history")
	public ResponseEntity<?> getWeather(@RequestParam String sido,
            			   @RequestParam String gugun,
            			   @RequestParam String eupmyeondong) {
		List<AlertDTO> alertList = alertService.getAlertRegionList(sido, gugun, eupmyeondong);
		return ResponseEntity.ok(alertList);
	}
	
}

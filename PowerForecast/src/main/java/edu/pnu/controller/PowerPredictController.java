package edu.pnu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.service.PowerPredictService;

@RestController
public class PowerPredictController {

	@Autowired
	PowerPredictService powerForecastService;
	
    @GetMapping("/send-data")
    public ResponseEntity<String> getPredict() {
       
        try {
			return ResponseEntity.ok(powerForecastService.getPredict());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
    }
}

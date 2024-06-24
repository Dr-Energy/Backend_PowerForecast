package edu.pnu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.DTO.PowerPredictDTO;
import edu.pnu.domain.PowerPrediction;
import edu.pnu.service.PowerPredictService;

@RestController
public class PowerPredictController {

	@Autowired
	PowerPredictService powerForecastService;
	
    @GetMapping("/predict")
    public ResponseEntity<?> getPredict() {
    	List<PowerPredictDTO> result = powerForecastService.getPredict();
    	if(result != null ) {
			return ResponseEntity.ok(result);
		} else {
			return ResponseEntity.badRequest().body("데이터가 없습니다.");
		}
    }
}

package edu.pnu.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.DTO.PowerPredictDTO;
import edu.pnu.domain.PowerPrediction;
import edu.pnu.service.PowerPredictService;

@RestController
public class PowerPredictController {

	@Autowired
	PowerPredictService powerForecastService;
	
	@GetMapping("/require/predict/{regionId}")
	public void requestPredict(@PathVariable Long regionId) throws ParseException {
		powerForecastService.requestPredict(regionId);
	}
	
    @GetMapping("/predict")
    public ResponseEntity<?> getOneDayPredict() {
    	List<PowerPredictDTO> result = powerForecastService.getOneDayPredict();
    	if(result != null ) {
			return ResponseEntity.ok(result);
		} else {
			return ResponseEntity.badRequest().body("데이터가 없습니다.");
		}
    }
    
    @GetMapping("/predict/currentTime")
    public ResponseEntity<?> getCurrentPredict() {
    	PowerPredictDTO result = powerForecastService.getCurrentPredict();
    	if(result != null ) {
			return ResponseEntity.ok(result);
		} else {
			return ResponseEntity.badRequest().body("데이터가 없습니다.");
		}
    }
    
    @GetMapping("/actual/currentTime")
    public ResponseEntity<?> getActualCurrent() {
    	PowerPredictDTO result = powerForecastService.getActualCurrent();
    	if(result != null ) {
			return ResponseEntity.ok(result);
		} else {
			return ResponseEntity.badRequest().body("데이터가 없습니다.");
		}
    }
    
//    @GetMapping
}

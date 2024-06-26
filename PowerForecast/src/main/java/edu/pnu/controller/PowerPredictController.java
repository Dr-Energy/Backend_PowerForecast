package edu.pnu.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@GetMapping("/predict/oneday")
	public ResponseEntity<?> getWeather( @RequestParam(required = false) String sido,
	        							 @RequestParam(required = false) String gugun,
	        							 @RequestParam(required = false) String eupmyeondong) throws Exception {
		try {
			 // 파라미터가 없을 때의 기본 동작을 정의하거나, 기본 값을 설정할 수 있습니다.
	        if (sido == null || gugun == null || eupmyeondong == null) {
	            // 기본 값을 설정하거나, 파라미터가 없을 때의 동작을 정의합니다.
	            sido = (sido == null) ? "서울특별시" : sido;	// 부산광역시
	            gugun = (gugun == null) ? "종로구" : gugun;		// 서구
	            eupmyeondong = (eupmyeondong == null) ? "청운효자동" : eupmyeondong;	// 암남동
	        }
	        
			return ResponseEntity.ok(powerForecastService.getOneDayPredict(sido, gugun, eupmyeondong));            
        } catch (Exception e) {
        	return ResponseEntity.badRequest().body(e.getMessage());
        } 
	}
	
	@GetMapping("/predict/oneday/{regionId}")
	public ResponseEntity<?> getWeather(@PathVariable Long regionId) throws Exception{
		try {
			return ResponseEntity.ok(powerForecastService.getOneDayPredict(regionId));            
        } catch (Exception e) {
        	return ResponseEntity.badRequest().body(e.getMessage());
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
    
}

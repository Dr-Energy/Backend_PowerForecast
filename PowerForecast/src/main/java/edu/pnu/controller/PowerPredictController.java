package edu.pnu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.pnu.service.PowerPredictService;

@RestController
public class PowerPredictController {

	@Autowired
	PowerPredictService powerForecastService;
	
	@GetMapping("/require/predict/{regionId}")
	public void requestPredict(@PathVariable Long regionId) throws Exception {
		powerForecastService.requestPredict(regionId);
	}
	
	@GetMapping("/predict/oneday")
	public ResponseEntity<?> getOneDayPredict( @RequestParam(required = false) String sido,
	        							 @RequestParam(required = false) String gugun,
	        							 @RequestParam(required = false) String eupmyeondong) throws Exception {
		System.out.println("[하루 예측 데이터 요청: 비로그인]");
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
	public ResponseEntity<?> getOneDayPredict(@PathVariable Long regionId) throws Exception{
		try {
			return ResponseEntity.ok(powerForecastService.getOneDayPredict(regionId));            
        } catch (Exception e) {
        	return ResponseEntity.badRequest().body(e.getMessage());
        }
	}
	
	@GetMapping("/predict/month")
	public ResponseEntity<?> getMonthPredict(@RequestParam(required = false) String sido,
			 								 @RequestParam(required = false) String gugun,
			 								 @RequestParam(required = false) String eupmyeondong) throws Exception{
		try {
			 // 파라미터가 없을 때의 기본 동작을 정의하거나, 기본 값을 설정할 수 있습니다.
	        if (sido == null || gugun == null || eupmyeondong == null) {
	            // 기본 값을 설정하거나, 파라미터가 없을 때의 동작을 정의합니다.
	            sido = (sido == null) ? "부산광역시" : sido;	// 부산광역시
	            gugun = (gugun == null) ? "서구" : gugun;		// 서구
	            eupmyeondong = (eupmyeondong == null) ? "암남동" : eupmyeondong;	// 암남동
	        }
			return ResponseEntity.ok(powerForecastService.getMonthPredict(sido, gugun, eupmyeondong));            
        } catch (Exception e) {
        	return ResponseEntity.badRequest().body(e.getMessage());
        }
	}
	
	@GetMapping("/predict/month/{regionId}")
	public ResponseEntity<?> getMonthPredict(@PathVariable Long regionId) throws Exception{
		try {
			return ResponseEntity.ok(powerForecastService.getMonthPredict(regionId));            
        } catch (Exception e) {
        	return ResponseEntity.badRequest().body(e.getMessage());
        }
	}
	
//    @GetMapping("/predict/currentTime")
//    public ResponseEntity<?> getCurrentPredict() {
//    	PowerPredictDTO result = powerForecastService.getCurrentPredict();
//    	if(result != null ) {
//			return ResponseEntity.ok(result);
//		} else {
//			return ResponseEntity.badRequest().body("데이터가 없습니다.");
//		}
//    }
//    
    @GetMapping("/predict/currentTime")
    public ResponseEntity<?> getCurrentPredict(@RequestParam(required = false) String sido,
			 									@RequestParam(required = false) String gugun,
			 									@RequestParam(required = false) String eupmyeondong) {
    	try {
			 // 파라미터가 없을 때의 기본 동작을 정의하거나, 기본 값을 설정할 수 있습니다.
	        if (sido == null || gugun == null || eupmyeondong == null) {
	            // 기본 값을 설정하거나, 파라미터가 없을 때의 동작을 정의합니다.
	            sido = (sido == null) ? "서울특별시" : sido;	// 부산광역시
	            gugun = (gugun == null) ? "종로구" : gugun;		// 서구
	            eupmyeondong = (eupmyeondong == null) ? "청운효자동" : eupmyeondong;	// 암남동
	        }
	        
			return ResponseEntity.ok(powerForecastService.getCurrentPredict(sido, gugun, eupmyeondong));            
       } catch (Exception e) {
       		return ResponseEntity.badRequest().body(e.getMessage());
       } 
    }
    
    @GetMapping("/predict/currentTime/{regionId}")
    public ResponseEntity<?> getCurrentPredict(@PathVariable Long regionId) throws Exception{
		try {
			return ResponseEntity.ok(powerForecastService.getCurrentPredict(regionId));            
        } catch (Exception e) {
        	return ResponseEntity.badRequest().body(e.getMessage());
        }
	}
    
    @GetMapping("/actual/currentTime")
    public ResponseEntity<?> getCurrentActual(@RequestParam(required = false) String sido,
											  @RequestParam(required = false) String gugun,
											  @RequestParam(required = false) String eupmyeondong) {
    	try {
			 // 파라미터가 없을 때의 기본 동작을 정의하거나, 기본 값을 설정할 수 있습니다.
	        if (sido == null || gugun == null || eupmyeondong == null) {
	            // 기본 값을 설정하거나, 파라미터가 없을 때의 동작을 정의합니다.
	            sido = (sido == null) ? "부산광역시" : sido;	// 부산광역시
	            gugun = (gugun == null) ? "서구" : gugun;		// 서구
	            eupmyeondong = (eupmyeondong == null) ? "암남동" : eupmyeondong;	// 암남동
	        }
			return ResponseEntity.ok(powerForecastService.getCurrentActual(sido, gugun, eupmyeondong));            
      } catch (Exception e) {
      		return ResponseEntity.badRequest().body(e.getMessage());
      } 
    }
    
    @GetMapping("/actual/currentTime/{regionId}")
    public ResponseEntity<?> getCurrentActual(@PathVariable Long regionId) throws Exception{
		try {
			return ResponseEntity.ok(powerForecastService.getCurrentActual(regionId));            
        } catch (Exception e) {
        	return ResponseEntity.badRequest().body(e.getMessage());
        }
	}
    
    
    
}

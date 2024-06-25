package edu.pnu.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.pnu.DTO.PowerPredictDTO;
import edu.pnu.domain.PowerPrediction;
import edu.pnu.domain.Weather;
import edu.pnu.persistence.PowerPredictionRepository;
import edu.pnu.persistence.WeatherRepository;

@Service
public class PowerPredictService {
	@Autowired
	private PowerPredictionRepository powerPreRepository;
	@Autowired
	private WeatherRepository weatherRepository;
	
	public List<PowerPredictDTO> getOneDayPredict() {
		List<PowerPrediction> preList = powerPreRepository.findAllByRequestSeq(1L);
		
		List<PowerPredictDTO> preDTOList = new ArrayList<>();
		for(PowerPrediction power:preList) {
//			System.out.println(power.getPredictTime());
			PowerPredictDTO dto = PowerPredictDTO.builder()
					.power(power.getPower())
					.time(power.getPredictTime())
					.build();
			preDTOList.add(dto);
		}
		return preDTOList;
	}
	
	public PowerPredictDTO getCurrentPredict() {
		
		Date convertedDate = createDate();
       
        
        // PowerPreRepository를 사용하여 Date를 기반으로 검색
        PowerPrediction prediction = powerPreRepository.findByPredictTime(convertedDate);
        PowerPredictDTO result = PowerPredictDTO.builder()
        		.power(prediction.getPower())
        		.time(prediction.getPredictTime())
        		.build();
		return result;
	}
	
	public PowerPredictDTO getActualCurrent() {
        Date convertedDate = createDate();
        
        // PowerPreRepository를 사용하여 Date를 기반으로 검색
        Weather weather = weatherRepository.findByTimestampAndGridNum(convertedDate, "5565");
        PowerPredictDTO result = PowerPredictDTO.builder()
        		.power(weather.getRealPower())
        		.time(weather.getTimestamp())
        		.build();
		return result;
	}
	
	private Date createDate() {
		// 특정 날짜
        String specificDate = "20210101";
        // 현재 시간
        LocalTime currentTime = LocalTime.now();
        // 시간을 정각으로 맞추기
        LocalTime roundedTime = currentTime.withMinute(0).withSecond(0).withNano(0);
        // 날짜 형식 지정
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        // LocalDate 객체 생성
        LocalDate date = LocalDate.parse(specificDate, dateFormatter);
        // LocalDateTime 객체 생성 (특정 날짜 + 정각 시간)
        LocalDateTime dateTime = LocalDateTime.of(date, roundedTime);
        // LocalDateTime을 Date로 변환
        Date convertedDate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        
        // 결과 출력
        System.out.println("Combined Date and Time: " + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return convertedDate;
	}
}

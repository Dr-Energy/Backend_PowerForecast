package edu.pnu.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import edu.pnu.DTO.FlaskReqDTO;
import edu.pnu.DTO.PowerPredictDTO;
import edu.pnu.domain.PowerPrediction;
import edu.pnu.domain.PredictRequest;
import edu.pnu.domain.Region;
import edu.pnu.domain.Weather;
import edu.pnu.persistence.PowerPredictionRepository;
import edu.pnu.persistence.PredictRequestRepository;
import edu.pnu.persistence.RegionRepository;
import edu.pnu.persistence.WeatherRepository;

@Service
public class PowerPredictService {
	@Autowired
	private PowerPredictionRepository powerPreRepository;
	@Autowired
	private WeatherRepository weatherRepository;
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private PredictRequestRepository predictReqRepository;
	@Autowired
	private RestTemplate restTemplate;

	public void requestPredict(Long regionId) throws ParseException {
		Optional<Region> regionOp = regionRepository.findById(regionId);
		String gridNum = "";
		if (regionOp.isEmpty()) {
			gridNum = "5566";
		} else {
			gridNum = regionOp.get().getGridNum();
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date startDate = dateFormat.parse("2022-01-01 00:00:00");
		Date endDate = dateFormat.parse("2022-01-02 00:00:00");

		List<Weather> weatherList = weatherRepository.findAllByGridNumAndTimestampBetween(gridNum, startDate, endDate);
		List<FlaskReqDTO> weatherDTOList = weatherList.stream().map(FlaskReqDTO::convertToDTO)
				.collect(Collectors.toList());

		// list확인하기 위한 출력
		for (FlaskReqDTO dto : weatherDTOList) {
			System.out.println(dto);
		}

		for (FlaskReqDTO dto : weatherDTOList) {
			ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:5000/receive-data", dto,
					String.class);
			System.out.println(response);
			System.out.println("=".repeat(50));
//			// 응답을 DB에 저장
//			Response responseEntity = new Response();
//			responseEntity.setWeatherId(weather.getWeatherId()); // Weather 엔티티의 ID 필드 이름에 맞게 변경
//			responseEntity.setResponse(response.getBody());
//			responseRepository.save(responseEntity);
		}

	}
	
	public List<PowerPredictDTO> getOneDayPredict(String sido, String gugun, String eupmyeondong) throws ParseException {
		Region region = regionRepository.findBySidoAndGugunAndEupmyeondong(sido, gugun, eupmyeondong).get();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse("2024-06-19");
		PredictRequest preReq = predictReqRepository.findByRegionRegionIdAndRequestDate(region.getRegionId(), date);
		
		if(preReq != null) {
			List<PowerPrediction> powerList = powerPreRepository.findByRequestSeq(preReq.getSeq());
			List<PowerPredictDTO> powerDTOList = powerList.stream().map(PowerPredictDTO::convertToDTO)
					.collect(Collectors.toList());
			System.out.println("[하루 예측 데이터 요청: 비로그인]");
//			for(PowerPredictDTO dto: powerDTOList)
//				System.out.println(dto);
			return powerDTOList;
		} else {
			// flask서버에 요청해서 결과 받아오기
		}
		
		return null;
	}
	
	public List<PowerPredictDTO> getOneDayPredict(Long regionId) throws ParseException {
		Region region = regionRepository.findById(regionId).get();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse("2024-06-26");
		PredictRequest preReq = predictReqRepository.findByRegionRegionIdAndRequestDate(region.getRegionId(), date);
		
		if(preReq != null) {
			List<PowerPrediction> powerList = powerPreRepository.findByRequestSeq(preReq.getSeq());
			List<PowerPredictDTO> powerDTOList = powerList.stream().map(PowerPredictDTO::convertToDTO)
					.collect(Collectors.toList());
			System.out.println("[하루 예측 데이터 요청: 로그인]");
//			for(PowerPredictDTO dto: powerDTOList)
//				System.out.println(dto);
			return powerDTOList;
		} else {
			// flask서버에 요청해서 결과 받아오기
		}
		
		return null;
	}

	public PowerPredictDTO getCurrentPredict() {

		Date convertedDate = createDate("20210101");

		// PowerPreRepository를 사용하여 Date를 기반으로 검색
		PowerPrediction prediction = powerPreRepository.findByPredictTime(convertedDate);
		PowerPredictDTO result = PowerPredictDTO.builder().power(prediction.getPower())
				.time(prediction.getPredictTime()).build();
		return result;
	}

	public PowerPredictDTO getActualCurrent() {
		Date convertedDate = createDate("20210101");

//        // PowerPreRepository를 사용하여 Date를 기반으로 검색
//        Weather weather = weatherRepository.findByTimestampAndGridNum(convertedDate, "5565");
//        PowerPredictDTO result = PowerPredictDTO.builder()
//        		.power(weather.getRealPower())
//        		.time(weather.getTimestamp())
//        		.build();
//		return result;
		return null;
	}

	private Date createDate(String defaultdate) {
		// 특정 날짜
		String specificDate = defaultdate;
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
		System.out.println(
				"Combined Date and Time: " + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		return convertedDate;
	}

	

	
}

package edu.pnu.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
		Region region = regionRepository.findById(regionId).get();
		
		Date today = createDate("20210101");
		PredictRequest predictReq = predictReqRepository.findByRegionRegionIdAndRequestDate(regionId, today);
		
		if (predictReq == null) {
			PredictRequest newReq = PredictRequest.builder()
					.region(region)
					.requestDate(today)
					.build();
			predictReqRepository.save(newReq);
			predictReq = newReq;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date startDate = dateFormat.parse("2022-01-01 00:00:00");
		Date endDate = dateFormat.parse("2022-01-02 00:00:00");
		
		List<Weather> weatherList = weatherRepository.findAllByGridNumAndTimestampBetween(region.getGridNum(), startDate, endDate);
		List<FlaskReqDTO> weatherDTOList = weatherList.stream().map(FlaskReqDTO::convertToDTO)
				.collect(Collectors.toList());

		// list확인하기 위한 출력
		for (FlaskReqDTO dto : weatherDTOList) {
			System.out.println(dto);
		}

		for (FlaskReqDTO dto : weatherDTOList) {
			ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:5000/predict", dto,
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
		System.out.println("[하루 예측 데이터 요청: 비로그인]");
		Region region = regionRepository.findBySidoAndGugunAndEupmyeondong(sido, gugun, eupmyeondong).get();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse("2024-06-19");
		
		List<PowerPredictDTO> result = getOneDayData(region.getRegionId(), date);
		return result;
	}
	
	public List<PowerPredictDTO> getOneDayPredict(Long regionId) throws ParseException {
		System.out.println("[하루 예측 데이터 요청: 로그인]");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse("2024-06-26");
		
		List<PowerPredictDTO> result = getOneDayData(regionId, date);
		return result;
	}
	
	private List<PowerPredictDTO> getOneDayData(Long regionId, Date date){
		PredictRequest preReq = predictReqRepository.findByRegionRegionIdAndRequestDate(regionId, date);
		
		if(preReq != null) {
			List<PowerPrediction> powerList = powerPreRepository.findByRequestSeq(preReq.getSeq());
			List<PowerPredictDTO> powerDTOList = powerList.stream().map(PowerPredictDTO::convertToDTO)
					.collect(Collectors.toList());

			return powerDTOList;
		} else {
			// flask서버에 요청해서 결과 받아오기
		}
		
		return null;
	}
	
	public List<PowerPredictDTO> getMonthPredict(Long regionId) throws Exception {
		Region region = regionRepository.findById(regionId).get();
		List<PowerPredictDTO> result = getDailyAverages(region.getGridNum());
        return result;
	}
	
	public List<PowerPredictDTO> getMonthPredict(String sido, String gugun, String eupmyeondong) throws Exception {
		Region region = regionRepository.findBySidoAndGugunAndEupmyeondong(sido, gugun, eupmyeondong).get();
		List<PowerPredictDTO> result = getDailyAverages(region.getGridNum());
        return result;
	}
	
	private List<PowerPredictDTO> getDailyAverages(String gridNum) throws Exception{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date startDate = dateFormat.parse("2021-12-01 00:00:00");
		Date endDate = dateFormat.parse("2021-12-31 23:00:00");
		List<Weather> powerList = weatherRepository.findAllByGridNumAndTimestampBetween(gridNum, startDate, endDate);
		// Group by date and calculate the average
        Map<String, List<PowerPredictDTO>> groupedByDate = powerList.stream()
                .map(PowerPredictDTO::convertToDTO)
                .collect(Collectors.groupingBy(dto -> new SimpleDateFormat("yyyy-MM-dd").format(dto.getTime())));

        List<PowerPredictDTO> dailyAverages = new ArrayList<>();

        for (Map.Entry<String, List<PowerPredictDTO>> entry : groupedByDate.entrySet()) {
            List<PowerPredictDTO> dailyData = entry.getValue();
            double averagePower = dailyData.stream().mapToDouble(PowerPredictDTO::getPower).average().orElse(0.0);

            PowerPredictDTO averageDTO = new PowerPredictDTO();
            averageDTO.setTime(dateFormat.parse(entry.getKey() + " 00:00:00"));
            averageDTO.setPower((float) averagePower);

            dailyAverages.add(averageDTO);
        }

        // Sort the daily averages by date
        dailyAverages.sort(Comparator.comparing(PowerPredictDTO::getTime));
        
        return dailyAverages;
	}

//	public PowerPredictDTO getCurrentPredict() {
//		
//		Date convertedDate = createDate("20210101");
//
//		// PowerPreRepository를 사용하여 Date를 기반으로 검색
//		PowerPrediction prediction = powerPreRepository.findByPredictTime(convertedDate);
//		PowerPredictDTO result = PowerPredictDTO.builder().power(prediction.getPower())
//				.time(prediction.getPredictTime()).build();
//		return result;
//	}
	
	public PowerPredictDTO getCurrentPredict(String sido, String gugun, String eupmyeondong) throws Exception {
		System.out.println("[현재시각 예측 데이터 요청: 비로그인]");
		Region region = regionRepository.findBySidoAndGugunAndEupmyeondong(sido, gugun, eupmyeondong).get();
		System.out.println(region.getRegionId());
		Date convertedDate = null;
		if(region.getRegionId().equals(1L) ) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse("2024-06-19");
			PredictRequest preReq = predictReqRepository.findByRegionRegionIdAndRequestDate(region.getRegionId(), date);
			convertedDate = createDate("20210101");
			PowerPrediction prediction = powerPreRepository.findByPredictTimeAndRequestSeq(convertedDate, preReq.getSeq());
			PowerPredictDTO result = PowerPredictDTO.builder().power(prediction.getPower())
					.time(prediction.getPredictTime()).build();
			return result;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse("2024-06-26");
			PredictRequest preReq = predictReqRepository.findByRegionRegionIdAndRequestDate(region.getRegionId(), date);
			convertedDate = createDate("20211231");
			PowerPrediction prediction = powerPreRepository.findByPredictTimeAndRequestSeq(convertedDate, preReq.getSeq());
			PowerPredictDTO result = PowerPredictDTO.builder().power(prediction.getPower())
					.time(prediction.getPredictTime()).build();
			return result;	
		}
	}
	
	public PowerPredictDTO getCurrentPredict(Long regionId) throws Exception {
		System.out.println("[현재시각 예측 데이터 요청: 로그인]");
		Date convertedDate = null;
		if(regionId.equals(1L) ) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse("2024-06-19");
			PredictRequest preReq = predictReqRepository.findByRegionRegionIdAndRequestDate(regionId, date);
			convertedDate = createDate("20210101");
			PowerPrediction prediction = powerPreRepository.findByPredictTimeAndRequestSeq(convertedDate, preReq.getSeq());
			PowerPredictDTO result = PowerPredictDTO.builder().power(prediction.getPower())
					.time(prediction.getPredictTime()).build();
			return result;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse("2024-06-26");
			PredictRequest preReq = predictReqRepository.findByRegionRegionIdAndRequestDate(regionId, date);
			convertedDate = createDate("20211231");
			PowerPrediction prediction = powerPreRepository.findByPredictTimeAndRequestSeq(convertedDate, preReq.getSeq());
			PowerPredictDTO result = PowerPredictDTO.builder().power(prediction.getPower())
					.time(prediction.getPredictTime()).build();
			return result;	
		}
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
	
	public PowerPredictDTO getCurrentActual(String sido, String gugun, String eupmyeondong) throws Exception {
		System.out.println("[현재시각 실제 데이터 요청: 비로그인]");
		Region region = regionRepository.findBySidoAndGugunAndEupmyeondong(sido, gugun, eupmyeondong).get();
		System.out.println("RegionId: " + region.getRegionId());
		System.out.println("GirdNum: " + region.getGridNum());
		
		Date convertedDate = createDate("20220101");
		Weather actual = weatherRepository.findByTimestampAndGridNum(convertedDate, region.getGridNum());
		PowerPredictDTO result = PowerPredictDTO.convertToDTO(actual);
		return result;
	}
	
	public PowerPredictDTO getCurrentActual(Long regionId) throws Exception {
		System.out.println("[현재시각 실제 데이터 요청: 로그인]");
		Region region = regionRepository.findById(regionId).get();
		System.out.println("RegionId: " + region.getRegionId());
		System.out.println("GirdNum: " + region.getGridNum());
		
		Date convertedDate = createDate("20220101");
		Weather actual = weatherRepository.findByTimestampAndGridNum(convertedDate, region.getGridNum());
		PowerPredictDTO result = PowerPredictDTO.convertToDTO(actual);
		return result;
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

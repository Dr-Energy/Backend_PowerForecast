package edu.pnu.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.pnu.DTO.FlaskReqDTO;
import edu.pnu.DTO.PowerPredictDTO;
import edu.pnu.DTO.Response.FlaskResDTO;
import edu.pnu.domain.Alert;
import edu.pnu.domain.AlertType;
import edu.pnu.domain.PowerPrediction;
import edu.pnu.domain.PredictRequest;
import edu.pnu.domain.Region;
import edu.pnu.domain.Weather;
import edu.pnu.persistence.AlertRepository;
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
	private AlertRepository alertRepository;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ObjectMapper objectMapper;

    private List<Long> getRegionIds() {
    	//448L, 469L, 470L, 553L, 556L, 557L,
        return Arrays.asList(
        		558L, 559L, 560L, 561L, 562L, 563L, 564L, 565L, 566L, 592L, 938L,
                940L, 949L, 950L, 951L, 952L, 953L, 954L, 955L, 956L, 957L, 958L, 959L, 960L, 961L, 962L, 963L, 964L,
                965L, 966L, 967L, 969L, 970L, 971L, 972L, 973L, 974L, 975L, 976L, 977L, 978L, 979L, 980L, 981L, 982L,
                983L, 984L, 985L, 986L, 988L, 989L, 990L, 1013L, 1014L, 1015L, 1016L, 1017L, 1018L, 1029L, 1030L, 2399L,
                2400L, 2401L, 2405L, 2406L, 2407L, 2408L, 2409L, 2410L, 2411L, 2414L, 2417L, 2420L, 2429L, 2430L, 2431L,
                2434L, 2435L, 2436L, 2437L, 2450L, 2470L, 2485L, 2599L, 2613L, 3034L, 3073L, 3076L, 3104L, 3111L, 3112L,
                3116L, 3117L, 3121L, 3123L, 3168L, 3169L, 3343L, 3355L, 3356L, 3357L, 3358L
        );
    }

	public void requestPredict(Long regionId) throws Exception {
		List<Long> regionIds = getRegionIds();
		
		for(Long id: regionIds) {
			System.out.println("Id: " + id);
			Region region = regionRepository.findById(id).get();

			Date today = createDate("20220101");
			PredictRequest predictReq = predictReqRepository.findByRegionRegionIdAndRequestDate(id, today);

			if (predictReq == null) {
				PredictRequest newReq = PredictRequest.builder().region(region).requestDate(today).build();
				predictReqRepository.save(newReq);
				predictReq = newReq;

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				Date startDate = dateFormat.parse("2022-01-01 00:00:00");
				Date endDate = dateFormat.parse("2022-01-01 23:00:00");

				List<Weather> weatherList = weatherRepository.findAllByGridNumAndTimestampBetween(region.getGridNum(),
						startDate, endDate);
				List<FlaskReqDTO> weatherDTOList = weatherList.stream().map(FlaskReqDTO::convertToDTO)
						.collect(Collectors.toList());
				
				Date prevDate = dateFormat.parse("2021-12-31 23:00:00");
				float prevPower = Float.parseFloat(weatherRepository.findByTimestampAndGridNum(prevDate, region.getGridNum())
																	.getElectPower());
				
				for (FlaskReqDTO dto : weatherDTOList) {
					
					ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:5000/predict", dto,
							String.class);
					System.out.println(response);
					System.out.println("=".repeat(50));
					// 응답을 처리하여 PredictionResult 엔티티에 저장
					if (response.getStatusCode() == HttpStatus.OK) {
						// JSON 응답을 FlaskResponseDTO로 변환
						FlaskResDTO flaskResponse = objectMapper.readValue(response.getBody(), FlaskResDTO.class);

						// 응답을 DB에 저장
						PowerPrediction powerPrediction = PowerPrediction.builder().request(predictReq)
								.predictTime(flaskResponse.getTimestamp()).power(flaskResponse.getPrediction()).build();

						powerPreRepository.save(powerPrediction);
						
						System.out.println("*".repeat(50));
						System.out.println("prev: " + prevPower);
						System.out.println("cur: " + powerPrediction.getPower());
						
						
						float usageIncrease = powerPrediction.getPower() - prevPower;
				        float increasePercentage = (usageIncrease / prevPower) * 100;
				        
				        System.out.println("increasePercent: " + increasePercentage);
				        System.out.println("*".repeat(50));
				        AlertType alertType = null;

				        if (increasePercentage >= 5) {
				            alertType = AlertType.ABNORMAL;
				        } else if (usageIncrease > 0) {
				            alertType = AlertType.INCREASE;
				        } else if (usageIncrease < 0) {
				            alertType = AlertType.DECREASE;
				        }

				        if (alertType != null) {
				            Alert alert = Alert.builder()
				            		.region(region)
				            		.alertTime(powerPrediction.getPredictTime())
				            		.alertType(alertType)
				            		.build();

				            alertRepository.save(alert);
				        }
				        
				        prevPower = powerPrediction.getPower();
					}
				}
			}
		}
		

	}
	
	public List<PowerPredictDTO> getOneDayPredict(String sido, String gugun, String eupmyeondong) throws ParseException {
		System.out.println("[하루 예측 데이터 요청: 비로그인]");
		Region region = regionRepository.findBySidoAndGugunAndEupmyeondong(sido, gugun, eupmyeondong).get();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse("2022-01-01");
		
		List<PowerPredictDTO> result = getOneDayData(region.getRegionId(), date);
		return result;
	}
	
	public List<PowerPredictDTO> getOneDayPredict(Long regionId) throws ParseException {
		System.out.println("[하루 예측 데이터 요청: 로그인]");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse("2022-01-01");
		
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
	
	public PowerPredictDTO getCurrentPredict(String sido, String gugun, String eupmyeondong) throws Exception {
		System.out.println("[현재시각 예측 데이터 요청: 비로그인]");
		Region region = regionRepository.findBySidoAndGugunAndEupmyeondong(sido, gugun, eupmyeondong).get();
		System.out.println(region.getRegionId());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse("2022-01-01");
		PredictRequest preReq = predictReqRepository.findByRegionRegionIdAndRequestDate(region.getRegionId(), date);
		Date convertedDate = createDate("20220101");
		PowerPrediction prediction = powerPreRepository.findByPredictTimeAndRequestSeq(convertedDate, preReq.getSeq());
		PowerPredictDTO result = PowerPredictDTO.builder().power(prediction.getPower())
				.time(prediction.getPredictTime()).build();
		return result;
	}
	
	public PowerPredictDTO getCurrentPredict(Long regionId) throws Exception {
		System.out.println("[현재시각 예측 데이터 요청: 로그인]");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse("2022-01-01");
		PredictRequest preReq = predictReqRepository.findByRegionRegionIdAndRequestDate(regionId, date);
		Date convertedDate = createDate("20220101");
		PowerPrediction prediction = powerPreRepository.findByPredictTimeAndRequestSeq(convertedDate, preReq.getSeq());
		PowerPredictDTO result = PowerPredictDTO.builder().power(prediction.getPower())
				.time(prediction.getPredictTime()).build();
		return result;
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

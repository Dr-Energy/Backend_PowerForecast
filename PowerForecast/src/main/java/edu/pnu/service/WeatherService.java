package edu.pnu.service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.pnu.DTO.WeatherDTO;
import edu.pnu.DTO.Response.SkyItemDTO;
import edu.pnu.domain.Region;
import edu.pnu.persistence.RegionRepository;

@Service
public class WeatherService {
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RegionRepository regionRepository;
	@Value("${basic-api-key}")
	private String basic_api_key;
	@Value("${decode-api-key}")
	private String decode_api_key;

	private String nx;
	private String ny;

	public ResponseEntity<?> getWeatherData(String sido, String gugun, String eupmyeondong)
			throws UnsupportedEncodingException, URISyntaxException, JsonMappingException, JsonProcessingException {
		Region regionInfo = regionRepository.findBySidoAndGugunAndEupmyeondong(sido, gugun, eupmyeondong).get();
		nx = regionInfo.getGridX();
		ny = regionInfo.getGridY();

		String[] dates = getDates();
        String curDate = dates[0];
        String baseDate = dates[1];
        System.out.println("Current Date: " + curDate);
        System.out.println("Previous Date: " + baseDate);
		String baseTime = "2300";
		String curTime = getCurrentTimeAdjusted();

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "JSON", Charset.forName("UTF-8")));
		URI uri = createURI(baseDate, baseTime);
		
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
			String jsonRes = response.getBody();
			
			// ObjectMapper로 JSON 문자열을 객체로 받아오기
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(jsonRes);
		    JsonNode itemsNode = root.path("response").path("body").path("items").path("item");
		    List<SkyItemDTO> items = objectMapper.readValue(itemsNode.toString(), new TypeReference<List<SkyItemDTO>>() {});

			WeatherDTO weather = filterWeatherData(items, curDate, curTime);
			return ResponseEntity.ok(weather);
		} catch (HttpClientErrorException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (RestClientException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	public URI createURI(String baseDate, String baseTime) throws UnsupportedEncodingException, URISyntaxException {
		String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
		String serviceKey = decode_api_key;
		String encodedServiceKey;
		encodedServiceKey = URLEncoder.encode(serviceKey, "UTF-8");
		StringBuilder builder = new StringBuilder(url);
		builder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + encodedServiceKey);
		builder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
		builder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8"));
		builder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8"));
		builder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8"));
		builder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8"));
		builder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8"));
		builder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8"));
		return new URI(builder.toString());
	}

	public static String[] getDates() {
        LocalDate now = LocalDate.now();
        LocalDate previousDay = now.minusDays(1);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        
        String currentDate = now.format(dateFormatter);
        String previousDate = previousDay.format(dateFormatter);
        
        return new String[] { currentDate, previousDate };
    }

	public String getCurrentTimeAdjusted() {
		// 현재 시간을 "HHmm" 형식의 문자열로 포맷
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
		String formattedTime = LocalDateTime.now().format(timeFormatter);

		// 시간을 정각으로 변환(10:35 -> 10:00)
		String adjustedTime = formattedTime.substring(0, 2) + "00";

		return adjustedTime;
	}
	
	private WeatherDTO filterWeatherData(List<SkyItemDTO> items, String date, String time) {
		HashMap<String, String> filterData = new HashMap<>();
		WeatherDTO weather;
		
        for (SkyItemDTO item : items) {
        	System.out.println(item);
            if (item.getFcstDate().equals(date) 
            	&& item.getFcstTime().equals(time) 
            	&& (item.getCategory().equals("SKY") || item.getCategory().equals("PTY"))) {
                // 해당 조건을 만족하는 데이터를 로깅합니다.
//                System.out.println("Found matching item: " + item);
                filterData.put(item.getCategory(), item.getFcstValue());
            }
        }

        if(filterData.get("PTY").equals("0")) {
        	weather = WeatherDTO.builder()
        			.category("SKY")
        			.value(filterData.get("SKY"))
        			.build();
        } else {
        	weather = WeatherDTO.builder()
        			.category("PTY")
        			.value(filterData.get("PTY"))
        			.build();
        }
        
        return weather;
    }
}

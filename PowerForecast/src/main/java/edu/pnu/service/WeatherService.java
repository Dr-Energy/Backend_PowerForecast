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
	@Value("${weather-api-key}")
	private String weather_api_key;

	private String nx;
	private String ny;
	private String lon;
	private String lat;

	public ResponseEntity<?> getWeatherData(String sido, String gugun, String eupmyeondong)
			throws UnsupportedEncodingException, URISyntaxException, JsonMappingException, JsonProcessingException {
		Region regionInfo = regionRepository.findBySidoAndGugunAndEupmyeondong(sido, gugun, eupmyeondong).get();
		nx = regionInfo.getGridX();
		ny = regionInfo.getGridY();
		lon = regionInfo.getLongitude();
		lat = regionInfo.getLatitude();

		String[] dates = getDates();
        String curDate = dates[0];
        String baseDate = dates[1];

		String baseTime = "2300";
		String curTime = getCurrentTimeAdjusted();

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "JSON", Charset.forName("UTF-8")));
		URI uri = createAWSURI(baseDate, baseTime);
		URI uri2 = createWeatherURI(curDate, curTime);
		System.out.println(uri2);
		
		try {
			ResponseEntity<String> response1 = restTemplate.getForEntity(uri, String.class);
			ResponseEntity<String> response2 = restTemplate.getForEntity(uri2, String.class);
			
			String jsonRes1 = response1.getBody();
			String jsonRes2 = response2.getBody();
			
			// ObjectMapper로 JSON 문자열을 객체로 받아오기
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(jsonRes1);
		    JsonNode itemsNode = root.path("response").path("body").path("items").path("item");
		    List<SkyItemDTO> items = objectMapper.readValue(itemsNode.toString(), new TypeReference<List<SkyItemDTO>>() {});

			WeatherDTO weather = filterWeatherData(items, curDate, curTime);
			weather = parseWeatherData(jsonRes2, weather);
			return ResponseEntity.ok(weather);
		} catch (HttpClientErrorException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (RestClientException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	public ResponseEntity<?> getWeatherData(Long regionId)
			throws UnsupportedEncodingException, URISyntaxException, JsonMappingException, JsonProcessingException {
		Region regionInfo = regionRepository.findById(regionId).get();
		nx = regionInfo.getGridX();
		ny = regionInfo.getGridY();
		lon = regionInfo.getLongitude();
		lat = regionInfo.getLatitude();

		String[] dates = getDates();
        String curDate = dates[0];
        String baseDate = dates[1];

		String baseTime = "2300";
		String curTime = getCurrentTimeAdjusted();

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "JSON", Charset.forName("UTF-8")));
		URI uri = createAWSURI(baseDate, baseTime);
		URI uri2 = createWeatherURI(curDate, curTime);
		System.out.println(uri2);
		
		try {
			ResponseEntity<String> response1 = restTemplate.getForEntity(uri, String.class);
			ResponseEntity<String> response2 = restTemplate.getForEntity(uri2, String.class);
			
			String jsonRes1 = response1.getBody();
			String jsonRes2 = response2.getBody();
			
			// ObjectMapper로 JSON 문자열을 객체로 받아오기
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(jsonRes1);
		    JsonNode itemsNode = root.path("response").path("body").path("items").path("item");
		    List<SkyItemDTO> items = objectMapper.readValue(itemsNode.toString(), new TypeReference<List<SkyItemDTO>>() {});

			WeatherDTO weather = filterWeatherData(items, curDate, curTime);
			weather = parseWeatherData(jsonRes2, weather);
			return ResponseEntity.ok(weather);
		} catch (HttpClientErrorException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (RestClientException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	public URI createAWSURI(String baseDate, String baseTime) throws UnsupportedEncodingException, URISyntaxException {
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
	
	public URI createWeatherURI(String curDate, String curTime) throws UnsupportedEncodingException, URISyntaxException {
		String url = "https://apihub.kma.go.kr/api/typ01/url/sfc_nc_var.php";
		String serviceKey = weather_api_key;
		String encodedServiceKey;
		encodedServiceKey = URLEncoder.encode(serviceKey, "UTF-8");
		StringBuilder builder = new StringBuilder(url);
		builder.append("?" + URLEncoder.encode("tm1", "UTF-8") + "=" + URLEncoder.encode(curDate+curTime, "UTF-8"));
		builder.append("&" + URLEncoder.encode("tm2", "UTF-8") + "=" + URLEncoder.encode(curDate+curTime, "UTF-8"));
		builder.append("&" + URLEncoder.encode("obs", "UTF-8") + "=" + URLEncoder.encode("ta,hm,rn_60m,ta_chi", "UTF-8"));
		builder.append("&" + URLEncoder.encode("itv", "UTF-8") + "=" + URLEncoder.encode("60", "UTF-8"));
		builder.append("&" + URLEncoder.encode("lon", "UTF-8") + "=" + URLEncoder.encode(lon, "UTF-8"));
		builder.append("&" + URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8"));
		builder.append("&" + URLEncoder.encode("help", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
		builder.append("&" + URLEncoder.encode("authKey", "UTF-8") + "=" + encodedServiceKey);
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
            if (item.getFcstDate().equals(date) 
            	&& item.getFcstTime().equals(time) 
            	&& (item.getCategory().equals("SKY") || item.getCategory().equals("PTY"))) {
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
	
	public static WeatherDTO parseWeatherData(String response, WeatherDTO weatherDTO) {
        // 응답에서 데이터 부분만 추출
        String dataSection = response.split("# tm, ta, hm, rn_60m, ta_chi")[1].trim();
        String[] lines = dataSection.split("\n");
        // 데이터 라인을 쉼표(,)로 분리
        String[] dataValues = lines[0].split(", ");
        
        
        // 데이터 값을 DTO에 설정
        weatherDTO.setTemp(dataValues[1]);
        weatherDTO.setHumidity(dataValues[2]);
        weatherDTO.setRain(dataValues[3]);
        weatherDTO.setBodyTemp(dataValues[4]);

        return weatherDTO;
    }
}

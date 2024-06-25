package edu.pnu.weather.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.pnu.DTO.WeatherDTO;
import edu.pnu.DTO.Response.SkyItemDTO;
import edu.pnu.domain.Region;

@Component
public class WeatherApiUrlBuilder {
	@Autowired
	private RestTemplate restTemplate;
	@Value("${basic-api-key}")
	private String basic_api_key;
	@Value("${decode-api-key}")
	private String decode_api_key;
	@Value("${weather-api-key}")
	private String weather_api_key;
	
	
	public List<WeatherDTO> getAPIData(Region regionInfo) throws Exception {

		String nx = regionInfo.getGridX();
		String ny = regionInfo.getGridY();
		String lon = regionInfo.getLongitude();
		String lat =  regionInfo.getLatitude();

		String[] dates = getDates();
		String curDate = dates[0];
		String baseDate = dates[1];

		String baseTime = "2300";
		String curTime = getCurrentTimeAdjusted();
		String itv = "1440";
		String obs = "ta,hm,rn_60m,ta_chi";

//		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "JSON", Charset.forName("UTF-8")));
		URI uri = createAWSURI(baseDate, baseTime, nx, ny);
		URI uri2 = createWeatherURI(baseDate, curDate, curTime, obs, itv, lon, lat);

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
		List<WeatherDTO> listWeather = parseWeatherData(jsonRes2, weather);
		System.out.println("[응답 완료]");
		return listWeather;

	}

	public URI createAWSURI(String baseDate, String baseTime, String nx, String ny) throws UnsupportedEncodingException, URISyntaxException {
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

	private URI createWeatherURI(String baseDate, String curDate, String curTime, String obs, String itv, String lon, String lat)
			throws UnsupportedEncodingException, URISyntaxException {
		String url = "https://apihub.kma.go.kr/api/typ01/url/sfc_nc_var.php";
		String serviceKey = weather_api_key;
		String encodedServiceKey;
		encodedServiceKey = URLEncoder.encode(serviceKey, "UTF-8");
		StringBuilder builder = new StringBuilder(url);
		builder.append("?" + URLEncoder.encode("tm1", "UTF-8") + "=" + URLEncoder.encode(baseDate + curTime, "UTF-8"));
		builder.append("&" + URLEncoder.encode("tm2", "UTF-8") + "=" + URLEncoder.encode(curDate + curTime, "UTF-8"));
		builder.append(
				"&" + URLEncoder.encode("obs", "UTF-8") + "=" + URLEncoder.encode(obs, "UTF-8"));
		builder.append("&" + URLEncoder.encode("itv", "UTF-8") + "=" + URLEncoder.encode(itv, "UTF-8"));
		builder.append("&" + URLEncoder.encode("lon", "UTF-8") + "=" + URLEncoder.encode(lon, "UTF-8"));
		builder.append("&" + URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8"));
		builder.append("&" + URLEncoder.encode("help", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
		builder.append("&" + URLEncoder.encode("authKey", "UTF-8") + "=" + encodedServiceKey);
		return new URI(builder.toString());
	}

	public String[] getDates() {
		LocalDate now = LocalDate.now();
		LocalDate previousDay = now.minusDays(1);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		String currentDate = now.format(dateFormatter);
		String previousDate = previousDay.format(dateFormatter);

		return new String[] { currentDate, previousDate };
	}

	private String getCurrentTimeAdjusted() {
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
			if (item.getFcstDate().equals(date) && item.getFcstTime().equals(time)
					&& (item.getCategory().equals("SKY") || item.getCategory().equals("PTY"))) {
				filterData.put(item.getCategory(), item.getFcstValue());
			}
		}

		if (filterData.get("PTY").equals("0")) {
			weather = WeatherDTO.builder().category("SKY").value(filterData.get("SKY")).build();
		} else {
			weather = WeatherDTO.builder().category("PTY").value(filterData.get("PTY")).build();
		}
		
		return weather;
	}

	private static List<WeatherDTO> parseWeatherData(String response, WeatherDTO curWeatherDTO) {
		// 응답에서 데이터 부분만 추출
		System.out.println(response);
		String dataSection = response.split("# tm, ta, hm, rn_60m, ta_chi")[1].trim();
		String[] lines = dataSection.split("\n");

		// 데이터 라인을 쉼표(,)로 분리
		String[] dataValues1 = lines[0].split(", ");
		String[] dataValues2 = lines[1].split(", ");

		WeatherDTO prevWeatherDTO = WeatherDTO.builder().temp(dataValues1[1]).humidity(dataValues1[2])
				.rain(dataValues1[3]).bodyTemp(dataValues1[4]).build();

		// 데이터 값을 DTO에 설정
		curWeatherDTO.setTemp(dataValues2[1]);
		curWeatherDTO.setHumidity(dataValues2[2]);
		curWeatherDTO.setRain(dataValues2[3]);
		curWeatherDTO.setBodyTemp(dataValues2[4]);

		List<WeatherDTO> result = new ArrayList<>();
		result.add(prevWeatherDTO);
		result.add(curWeatherDTO);
		return result;
	}
}

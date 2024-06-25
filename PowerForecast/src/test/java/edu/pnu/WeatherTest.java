package edu.pnu;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import edu.pnu.domain.Weather;
import edu.pnu.persistence.WeatherRepository;

@SpringBootTest
public class WeatherTest {
	@Autowired
	WeatherRepository weatherRepository;
	
	@Test
	public void getActualPower() {
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
        System.out.println(dateTime);
		Weather weather = weatherRepository.findByTimestampAndGridNum(convertedDate, "5565");
		System.out.println(weather);
	}
	
//	@Test
	public void getAll() {
		List<Weather> list = weatherRepository.findAll();
		for(Weather weather:list) {
			System.out.println(weather);
		}
	}
}

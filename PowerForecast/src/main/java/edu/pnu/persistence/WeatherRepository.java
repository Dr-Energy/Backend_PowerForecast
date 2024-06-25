package edu.pnu.persistence;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pnu.domain.Weather;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
	Weather findByTimestampAndGridNum(Date time, String gridNum);
	List<Weather> findAllByGridNumAndTimestampBetween(String girdNum, Date start, Date end);
}

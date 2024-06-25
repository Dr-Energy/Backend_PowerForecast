package edu.pnu.persistence;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pnu.domain.Weather;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
	Weather findByTimestampAndGridNum(Date time, String gridNum);
}

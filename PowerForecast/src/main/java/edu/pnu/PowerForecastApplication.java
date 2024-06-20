package edu.pnu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class PowerForecastApplication {

	public static void main(String[] args) {
		SpringApplication.run(PowerForecastApplication.class, args);
	}

}

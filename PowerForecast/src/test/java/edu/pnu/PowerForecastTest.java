package edu.pnu;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import edu.pnu.domain.Alert;
import edu.pnu.domain.Member;
import edu.pnu.domain.Powerforecast;
import edu.pnu.domain.Region;
import edu.pnu.persistence.AlertRepository;
import edu.pnu.persistence.PowerforecastRepository;
import edu.pnu.persistence.RegionRepository;

@SpringBootTest
public class PowerForecastTest {
	@Autowired
	PowerforecastRepository powerRepo;
	@Autowired
	RegionRepository regionRepo;
	
//	@Test
	public void addAlert() {
		Region region = regionRepo.findById(1L).get();
		
		Powerforecast powerF = Powerforecast.builder()
				.region(region)
				.predictedPower(1658.28)
				.build();
		
		powerRepo.save(powerF);
	}
	
	@Test
	public void getMemberList() {
		List<Powerforecast> powers = powerRepo.findAll();
		
		for(int i=0; i<powers.size(); i++) {
			System.out.println(powers.get(i));
		}
	}
}

package edu.pnu;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import edu.pnu.domain.PowerPrediction;
import edu.pnu.domain.PredictRequest;
import edu.pnu.domain.Region;
import edu.pnu.persistence.PowerPredictionRepository;
import edu.pnu.persistence.PredictRequestRepository;
import edu.pnu.persistence.RegionRepository;

@SpringBootTest
public class PowerPredict {
	@Autowired
	private PredictRequestRepository predictRepo;
	@Autowired
	private RegionRepository regionRepo;
	@Autowired
	private PowerPredictionRepository powerPredictRepo;
	
//	@Test
	public void addRequest() {
		Region region = regionRepo.findById(1L).get();
		PredictRequest req = PredictRequest.builder()
				.region(region)
				.requestDate(new Date())
				.build();
		predictRepo.save(req);
	}
	
	@Test
	public void addPredict() {
		PredictRequest req = predictRepo.findById(1L).get();
		PowerPrediction predict = PowerPrediction.builder()
				.request(req)
				.power(94.6f)
				.predictTime(new Date())
				.build();
		powerPredictRepo.save(predict);
	}
}

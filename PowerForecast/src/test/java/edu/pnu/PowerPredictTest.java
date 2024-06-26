package edu.pnu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
public class PowerPredictTest {
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
	
//	@Test
	public void addPredict() {
		PredictRequest req = predictRepo.findById(1L).get();
		PowerPrediction predict = PowerPrediction.builder()
				.request(req)
				.power(94.6F)
				.predictTime(new Date())
				.build();
		powerPredictRepo.save(predict);
	}
	
//	@Test
	public void testFindByRegionIdAndRequestDate() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse("2024-06-19");

		Long regionId = 1L;

		PredictRequest result = predictRepo.findByRegionRegionIdAndRequestDate(regionId, date);

		System.out.println(result);
	}
}

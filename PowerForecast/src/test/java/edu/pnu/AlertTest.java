package edu.pnu;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import edu.pnu.domain.Alert;
import edu.pnu.domain.Region;
import edu.pnu.persistence.AlertRepository;
import edu.pnu.persistence.RegionRepository;

@SpringBootTest
public class AlertTest {
	@Autowired
	AlertRepository alertRepo;
	@Autowired
	RegionRepository regionRepo;

	@Test
	public void addAlert() {
		Region region = regionRepo.findById(3343L).get();

		for (int i = 1; i <= 8; i++) {
			Alert alert = Alert.builder().region(region).build();

			alertRepo.save(alert);
		}

//		region = regionRepo.findById(3L).get();
//		for (int i = 1; i <= 5; i++) {
//			Alert alert = Alert.builder().region(region).build();
//
//			alertRepo.save(alert);
//		}
//
//		region = regionRepo.findById(15L).get();
//		for (int i = 1; i <= 5; i++) {
//			Alert alert = Alert.builder().region(region).build();
//
//			alertRepo.save(alert);
//		}
	}

//	@Test
	public void getMemberList() {
		List<Alert> alerts = alertRepo.findAll();

		for (int i = 0; i < alerts.size(); i++) {
			System.out.println(alerts.get(i));
		}
	}

//	@Test
	public void addOneAlert() {
		Region region = regionRepo.findById(3L).get();

		Alert alert = Alert.builder().region(region).build();

		alertRepo.save(alert);
	}
	
	@Test
	public void getAlertList() {
//		List<Alert> alerts = alertRepo.findByRegionRegionIdOrderByAlertTimeDesc(1L);
		List<Alert> alerts = alertRepo.findAll(Sort.by(Direction.ASC, "alertTime"));

		for(Alert alert:alerts) {
			System.out.println(alert);
		}
	}
}

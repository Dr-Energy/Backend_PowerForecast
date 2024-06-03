package edu.pnu;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import edu.pnu.domain.Region;
import edu.pnu.persistence.RegionRepository;

@SpringBootTest
public class RegionTest {
	@Autowired
	RegionRepository regionRepository;
	
	@Test
	public void getRegionList() {
		List<Region> regions = regionRepository.findAll();
		
		for(int i=0; i<=20; i++) {
			System.out.println(regions.get(i));
		}
	}
}

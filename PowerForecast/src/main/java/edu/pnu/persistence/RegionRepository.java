package edu.pnu.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pnu.domain.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {
	Optional<Region> findBySidoAndGugunAndEupmyeondong(String region1, String region2, String region3);
}

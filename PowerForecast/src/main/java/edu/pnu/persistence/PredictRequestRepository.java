package edu.pnu.persistence;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.pnu.domain.PredictRequest;

public interface PredictRequestRepository extends JpaRepository<PredictRequest, Long> {
	@Query("SELECT p FROM PredictRequest p WHERE p.region.regionId = :regionId AND FUNCTION('DATE', p.requestDate) = :requestDate")
    PredictRequest findByRegionRegionIdAndRequestDate(@Param("regionId") Long regionId, @Param("requestDate") Date requestDate);
	PredictRequest findByRegionRegionId(Long regionId);
}

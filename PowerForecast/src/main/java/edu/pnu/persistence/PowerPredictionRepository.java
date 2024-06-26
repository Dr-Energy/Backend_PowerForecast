package edu.pnu.persistence;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pnu.domain.PowerPrediction;

public interface PowerPredictionRepository extends JpaRepository<PowerPrediction, Long> {
	List<PowerPrediction> findByRequestSeq(Long requestId);
	PowerPrediction findByPredictTime(Date time);
}

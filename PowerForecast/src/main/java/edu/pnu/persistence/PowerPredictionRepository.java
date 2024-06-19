package edu.pnu.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pnu.domain.PowerPrediction;

public interface PowerPredictionRepository extends JpaRepository<PowerPrediction, Long> {

}

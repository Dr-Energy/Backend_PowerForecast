package edu.pnu.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pnu.domain.PredictRequest;

public interface PredictRequestRepository extends JpaRepository<PredictRequest, Long> {

}

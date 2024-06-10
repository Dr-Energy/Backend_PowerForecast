package edu.pnu.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pnu.domain.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {
	List<Alert> findAllByRegionRegionId(Long regionId);
}

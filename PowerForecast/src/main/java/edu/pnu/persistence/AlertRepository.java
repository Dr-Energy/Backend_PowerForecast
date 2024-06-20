package edu.pnu.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import edu.pnu.domain.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {
//	@Query("SELECT a FROM Alert a WHERE a.region.regionId = :regionId ORDER BY a.alertTime DESC")
//    List<Alert> findByRegionRegionIdOrderByAlertTimeDesc(@Param("regionId") Long regionId);   
	@Query("SELECT a FROM Alert a ORDER BY a.alertTime DESC")
    List<Alert> findAllOrderByAlertTimeDesc();    
	
	List<Alert> findByRegionRegionIdOrderByAlertTimeDesc(Long regionId);
}

package edu.pnu.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pnu.domain.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {

}

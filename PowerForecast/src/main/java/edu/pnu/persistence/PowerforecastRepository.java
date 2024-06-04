package edu.pnu.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pnu.domain.Powerforecast;

public interface PowerforecastRepository extends JpaRepository<Powerforecast, Long>{

}

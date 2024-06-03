package edu.pnu.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.pnu.domain.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {

}
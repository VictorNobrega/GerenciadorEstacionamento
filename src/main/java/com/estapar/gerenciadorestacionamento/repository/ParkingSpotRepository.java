package com.estapar.gerenciadorestacionamento.repository;

import com.estapar.gerenciadorestacionamento.domain.ParkingSpot;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

	Optional<ParkingSpot> findByLatAndLng(BigDecimal lat, BigDecimal lng);

	long countBySectorCodeAndOccupiedTrue(String sectorCode);

	@Query("select count(s) > 0 from ParkingSpot s where s.sector.code = :sectorCode and s.occupied = false")
	boolean hasAvailableSpot(@Param("sectorCode") String sectorCode);
}

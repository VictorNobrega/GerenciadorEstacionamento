package com.estapar.gerenciadorestacionamento.repository;

import com.estapar.gerenciadorestacionamento.domain.ParkingSpot;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

	Optional<ParkingSpot> findByLatAndLng(BigDecimal lat, BigDecimal lng);

	long countBySectorCodeAndOccupiedTrue(String sectorCode);
}

package com.estapar.gerenciadorestacionamento.service;

import com.estapar.gerenciadorestacionamento.domain.ParkingSpot;
import com.estapar.gerenciadorestacionamento.domain.Sector;
import com.estapar.gerenciadorestacionamento.dto.GarageResponse;
import com.estapar.gerenciadorestacionamento.exception.BusinessException;
import com.estapar.gerenciadorestacionamento.repository.ParkingSpotRepository;
import com.estapar.gerenciadorestacionamento.repository.SectorRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("!test")
public class GarageBootstrapService implements CommandLineRunner {

	private final RestTemplate restTemplate;
	private final EntityManager entityManager;
	private final SectorRepository sectorRepository;
	private final ParkingSpotRepository parkingSpotRepository;
	private final String simulatorUrl;

	public GarageBootstrapService(
			RestTemplate restTemplate,
			EntityManager entityManager,
			SectorRepository sectorRepository,
			ParkingSpotRepository parkingSpotRepository,
			@Value("${garage.simulator.url}") String simulatorUrl
	) {
		this.restTemplate = restTemplate;
		this.entityManager = entityManager;
		this.sectorRepository = sectorRepository;
		this.parkingSpotRepository = parkingSpotRepository;
		this.simulatorUrl = simulatorUrl;
	}

	@Override
	@Transactional
	public void run(String... args) {
		clearDatabase();

		GarageResponse response;
		try {
			response = restTemplate.getForObject(simulatorUrl + "/garage", GarageResponse.class);
		} catch (RestClientException ex) {
			throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to load garage data from simulator");
		}

		if (response == null) {
			throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, "Simulator returned an empty garage response");
		}

		for (GarageResponse.GarageSectorResponse sectorResponse : response.garage()) {
			Sector sector = sectorRepository.findById(sectorResponse.sector())
					.orElseGet(() -> new Sector(sectorResponse.sector(), sectorResponse.basePrice(), sectorResponse.maxCapacity()));
			sector.update(sectorResponse.basePrice(), sectorResponse.maxCapacity());
			sectorRepository.save(sector);
		}

		Map<String, Sector> sectors = sectorRepository.findAll().stream()
				.collect(Collectors.toMap(Sector::getCode, Function.identity()));

		for (GarageResponse.GarageSpotResponse spotResponse : response.spots()) {
			Sector sector = sectors.get(spotResponse.sector());
			if (sector == null) {
				throw new BusinessException(HttpStatus.BAD_REQUEST, "Spot references an unknown sector: " + spotResponse.sector());
			}

			ParkingSpot spot = parkingSpotRepository.findById(spotResponse.id())
					.orElseGet(() -> new ParkingSpot(spotResponse.id(), sector, spotResponse.lat(), spotResponse.lng()));
			spot.update(sector, spotResponse.lat(), spotResponse.lng());
			parkingSpotRepository.save(spot);
		}
	}

	private void clearDatabase() {
		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
		entityManager.createNativeQuery("TRUNCATE TABLE vehicle_stays").executeUpdate();
		entityManager.createNativeQuery("TRUNCATE TABLE parking_spots").executeUpdate();
		entityManager.createNativeQuery("TRUNCATE TABLE sectors").executeUpdate();
		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
		entityManager.clear();
	}
}

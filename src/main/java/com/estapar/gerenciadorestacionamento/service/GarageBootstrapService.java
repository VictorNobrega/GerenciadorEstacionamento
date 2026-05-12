package com.estapar.gerenciadorestacionamento.service;

import com.estapar.gerenciadorestacionamento.domain.ParkingSpot;
import com.estapar.gerenciadorestacionamento.domain.Sector;
import com.estapar.gerenciadorestacionamento.dto.GarageResponse;
import com.estapar.gerenciadorestacionamento.exception.BusinessException;
import com.estapar.gerenciadorestacionamento.repository.ParkingSpotRepository;
import com.estapar.gerenciadorestacionamento.repository.SectorRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
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

	private static final String GARAGE_ENDPOINT = "/garage";

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
		GarageResponse response;
		try {
			response = restTemplate.getForObject(simulatorUrl + GARAGE_ENDPOINT, GarageResponse.class);
		} catch (RestClientException ex) {
			throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to load garage data from simulator");
		}

		if (response == null) {
			throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, "Simulator returned an empty garage response");
		}
		validateGarageResponse(response);

		clearDatabase();

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

	private void validateGarageResponse(GarageResponse response) {
		if (response.garage() == null || response.garage().isEmpty()) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "Garage response must include at least one sector");
		}
		if (response.spots() == null || response.spots().isEmpty()) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "Garage response must include at least one spot");
		}
		response.garage().forEach(this::validateSector);
		response.spots().forEach(this::validateSpot);
	}

	private void validateSector(GarageResponse.GarageSectorResponse sector) {
		if (sector == null) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "Garage sector is required");
		}
		if (isBlank(sector.sector())) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "Garage sector code is required");
		}
		if (sector.basePrice() == null || sector.basePrice().compareTo(BigDecimal.ZERO) <= 0) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "Garage sector base price must be positive");
		}
		if (sector.maxCapacity() <= 0) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "Garage sector max capacity must be positive");
		}
	}

	private void validateSpot(GarageResponse.GarageSpotResponse spot) {
		if (spot == null) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "Garage spot is required");
		}
		if (spot.id() == null) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "Garage spot id is required");
		}
		if (isBlank(spot.sector())) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "Garage spot sector is required");
		}
		if (spot.lat() == null || spot.lng() == null) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "Garage spot coordinates are required");
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
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

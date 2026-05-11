package com.estapar.gerenciadorestacionamento.service;

import com.estapar.gerenciadorestacionamento.domain.ParkingSpot;
import com.estapar.gerenciadorestacionamento.domain.VehicleStay;
import com.estapar.gerenciadorestacionamento.dto.WebhookRecordResponse;
import com.estapar.gerenciadorestacionamento.dto.WebhookRequest;
import com.estapar.gerenciadorestacionamento.exception.BusinessException;
import com.estapar.gerenciadorestacionamento.repository.ParkingSpotRepository;
import com.estapar.gerenciadorestacionamento.repository.VehicleStayRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class WebhookService {

	private final VehicleStayRepository vehicleStayRepository;
	private final ParkingSpotRepository parkingSpotRepository;
	private final PricingService pricingService;

	public WebhookService(
			VehicleStayRepository vehicleStayRepository,
			ParkingSpotRepository parkingSpotRepository,
			PricingService pricingService
	) {
		this.vehicleStayRepository = vehicleStayRepository;
		this.parkingSpotRepository = parkingSpotRepository;
		this.pricingService = pricingService;
	}

	@Transactional
	public void handle(WebhookRequest request) {
		switch (request.eventType()) {
			case ENTRY -> handleEntry(request);
			case PARKED -> handleParked(request);
			case EXIT -> handleExit(request);
		}
	}

	public List<WebhookRecordResponse> listRecords() {
		return vehicleStayRepository.findAllByOrderByEntryTimeDescIdDesc()
				.stream()
				.map(this::toResponse)
				.toList();
	}

	private WebhookRecordResponse toResponse(VehicleStay stay) {
		ParkingSpot spot = stay.getSpot();
		return new WebhookRecordResponse(
				stay.getId(),
				stay.getLicensePlate(),
				stay.getEntryTime(),
				stay.getExitTime(),
				stay.getSector() == null ? null : stay.getSector().getCode(),
				spot == null ? null : spot.getId(),
				spot == null ? null : spot.getLat(),
				spot == null ? null : spot.getLng(),
				stay.getHourlyPrice(),
				stay.getAmount(),
				stay.getLastEventType()
		);
	}

	private void handleEntry(WebhookRequest request) {
		if (request.entryTime() == null) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "entry_time is required for ENTRY events");
		}

		VehicleStay stay = vehicleStayRepository
				.findFirstByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(request.licensePlate())
				.orElseGet(() -> new VehicleStay(request.licensePlate(), request.entryTime()));
		stay.refreshEntryTime(request.entryTime());
		vehicleStayRepository.save(stay);
	}

	private void handleParked(WebhookRequest request) {
		if (request.lat() == null || request.lng() == null) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "lat and lng are required for PARKED events");
		}

		VehicleStay stay = vehicleStayRepository
				.findFirstByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(request.licensePlate())
				.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "No active entry found for vehicle"));

		ParkingSpot spot = parkingSpotRepository.findByLatAndLng(request.lat(), request.lng())
				.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Parking spot not found"));

		if (stay.getSpot() != null && stay.getSpot().getId().equals(spot.getId())) {
			return;
		}
		if (spot.isOccupied()) {
			throw new BusinessException(HttpStatus.CONFLICT, "Parking spot is already occupied");
		}

		long occupied = parkingSpotRepository.countBySectorCodeAndOccupiedTrue(spot.getSector().getCode());
		if (occupied >= spot.getSector().getMaxCapacity() || !parkingSpotRepository.hasAvailableSpot(spot.getSector().getCode())) {
			throw new BusinessException(HttpStatus.CONFLICT, "Sector is full");
		}

		BigDecimal hourlyPrice = pricingService.dynamicHourlyPrice(spot.getSector(), occupied);
		spot.occupy();
		stay.park(spot, hourlyPrice);
		parkingSpotRepository.save(spot);
		vehicleStayRepository.save(stay);
	}

	private void handleExit(WebhookRequest request) {
		if (request.exitTime() == null) {
			throw new BusinessException(HttpStatus.BAD_REQUEST, "exit_time is required for EXIT events");
		}

		VehicleStay stay = vehicleStayRepository
				.findFirstByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(request.licensePlate())
				.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "No active stay found for vehicle"));

		BigDecimal hourlyPrice = stay.getHourlyPrice() == null ? BigDecimal.ZERO : stay.getHourlyPrice();
		BigDecimal amount = pricingService.calculateAmount(stay.getEntryTime(), request.exitTime(), hourlyPrice);
		if (stay.getSpot() != null) {
			stay.getSpot().release();
			parkingSpotRepository.save(stay.getSpot());
		}
		stay.close(request.exitTime(), amount);
		vehicleStayRepository.save(stay);
	}
}

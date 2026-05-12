package com.estapar.gerenciadorestacionamento.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.estapar.gerenciadorestacionamento.domain.ParkingSpot;
import com.estapar.gerenciadorestacionamento.domain.Sector;
import com.estapar.gerenciadorestacionamento.dto.WebhookRequest;
import com.estapar.gerenciadorestacionamento.enums.EventType;
import com.estapar.gerenciadorestacionamento.exception.BusinessException;
import com.estapar.gerenciadorestacionamento.repository.ParkingSpotRepository;
import com.estapar.gerenciadorestacionamento.repository.SectorRepository;
import com.estapar.gerenciadorestacionamento.repository.VehicleStayRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class WebhookServiceIntegrationTest {

	@Autowired
	private WebhookService webhookService;

	@Autowired
	private SectorRepository sectorRepository;

	@Autowired
	private ParkingSpotRepository parkingSpotRepository;

	@Autowired
	private VehicleStayRepository vehicleStayRepository;

	@BeforeEach
	void setUp() {
		vehicleStayRepository.deleteAll();
		parkingSpotRepository.deleteAll();
		sectorRepository.deleteAll();

		Sector sector = sectorRepository.save(new Sector("A", BigDecimal.valueOf(10), 2));
		parkingSpotRepository.save(new ParkingSpot(1L, sector, BigDecimal.valueOf(-23.561684), BigDecimal.valueOf(-46.655981)));
		parkingSpotRepository.save(new ParkingSpot(2L, sector, BigDecimal.valueOf(-23.561664), BigDecimal.valueOf(-46.655961)));
	}

	@Test
	void handlesEntryParkedAndExitFlow() {
		webhookService.handle(new WebhookRequest("ZUL0001", Instant.parse("2025-01-01T12:00:00Z"), null, null, null, EventType.ENTRY));
		webhookService.handle(new WebhookRequest("ZUL0001", null, null, BigDecimal.valueOf(-23.561684), BigDecimal.valueOf(-46.655981), EventType.PARKED));
		webhookService.handle(new WebhookRequest("ZUL0001", null, Instant.parse("2025-01-01T13:01:00Z"), null, null, EventType.EXIT));

		var stay = vehicleStayRepository.findAll().getFirst();
		assertThat(stay.getAmount()).isEqualByComparingTo("18.00");
		assertThat(stay.getExitTime()).isNotNull();
		assertThat(parkingSpotRepository.findById(1L).orElseThrow().isOccupied()).isFalse();
	}

	@Test
	void rejectsParkingWhenSpotIsAlreadyOccupied() {
		webhookService.handle(new WebhookRequest("ZUL0001", Instant.parse("2025-01-01T12:00:00Z"), null, null, null, EventType.ENTRY));
		webhookService.handle(new WebhookRequest("ZUL0001", null, null, BigDecimal.valueOf(-23.561684), BigDecimal.valueOf(-46.655981), EventType.PARKED));
		webhookService.handle(new WebhookRequest("ZUL0002", Instant.parse("2025-01-01T12:10:00Z"), null, null, null, EventType.ENTRY));

		assertThatThrownBy(() -> webhookService.handle(new WebhookRequest("ZUL0002", null, null, BigDecimal.valueOf(-23.561684), BigDecimal.valueOf(-46.655981), EventType.PARKED)))
				.isInstanceOf(BusinessException.class)
				.extracting("status")
				.isEqualTo(HttpStatus.CONFLICT);
	}

	@Test
	void rejectsEntryOnlyWhenAllSectorsAreFull() {
		Sector sectorB = sectorRepository.save(new Sector("B", BigDecimal.valueOf(20), 1));
		parkingSpotRepository.save(new ParkingSpot(3L, sectorB, BigDecimal.valueOf(-23.561644), BigDecimal.valueOf(-46.655941)));

		webhookService.handle(new WebhookRequest("ZUL0001", Instant.parse("2025-01-01T12:00:00Z"), null, null, null, EventType.ENTRY));
		webhookService.handle(new WebhookRequest("ZUL0001", null, null, BigDecimal.valueOf(-23.561684), BigDecimal.valueOf(-46.655981), EventType.PARKED));
		webhookService.handle(new WebhookRequest("ZUL0002", Instant.parse("2025-01-01T12:10:00Z"), null, null, null, EventType.ENTRY));
		webhookService.handle(new WebhookRequest("ZUL0002", null, null, BigDecimal.valueOf(-23.561664), BigDecimal.valueOf(-46.655961), EventType.PARKED));

		webhookService.handle(new WebhookRequest("ZUL0003", Instant.parse("2025-01-01T12:20:00Z"), null, null, null, EventType.ENTRY));
		webhookService.handle(new WebhookRequest("ZUL0003", null, null, BigDecimal.valueOf(-23.561644), BigDecimal.valueOf(-46.655941), EventType.PARKED));

		assertThatThrownBy(() -> webhookService.handle(new WebhookRequest("ZUL0004", Instant.parse("2025-01-01T12:30:00Z"), null, null, null, EventType.ENTRY)))
				.isInstanceOf(BusinessException.class)
				.extracting("status")
				.isEqualTo(HttpStatus.CONFLICT);
	}
}

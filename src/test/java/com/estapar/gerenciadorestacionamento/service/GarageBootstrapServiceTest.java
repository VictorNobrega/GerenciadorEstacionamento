package com.estapar.gerenciadorestacionamento.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.estapar.gerenciadorestacionamento.dto.GarageResponse;
import com.estapar.gerenciadorestacionamento.exception.BusinessException;
import com.estapar.gerenciadorestacionamento.repository.ParkingSpotRepository;
import com.estapar.gerenciadorestacionamento.repository.SectorRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

class GarageBootstrapServiceTest {

	@Test
	void rejectsInvalidGarageResponseBeforeClearingDatabase() {
		RestTemplate restTemplate = mock(RestTemplate.class);
		EntityManager entityManager = mock(EntityManager.class);
		GarageBootstrapService service = new GarageBootstrapService(
				restTemplate,
				entityManager,
				mock(SectorRepository.class),
				mock(ParkingSpotRepository.class),
				"http://localhost:3000"
		);

		when(restTemplate.getForObject(anyString(), eq(GarageResponse.class)))
				.thenReturn(new GarageResponse(
						List.of(),
						List.of(new GarageResponse.GarageSpotResponse(1L, "A", BigDecimal.ONE, BigDecimal.TEN))
				));

		assertThatThrownBy(service::run)
				.isInstanceOf(BusinessException.class)
				.satisfies(ex -> {
					BusinessException businessException = (BusinessException) ex;
					assertThat(businessException.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
					assertThat(businessException.getMessage()).isEqualTo("Garage response must include at least one sector");
				});

		verify(entityManager, never()).createNativeQuery(anyString());
	}

	@Test
	void rejectsSpotWithoutCoordinates() {
		RestTemplate restTemplate = mock(RestTemplate.class);
		EntityManager entityManager = entityManager();
		GarageBootstrapService service = new GarageBootstrapService(
				restTemplate,
				entityManager,
				mock(SectorRepository.class),
				mock(ParkingSpotRepository.class),
				"http://localhost:3000"
		);

		when(restTemplate.getForObject(anyString(), eq(GarageResponse.class)))
				.thenReturn(new GarageResponse(
						List.of(new GarageResponse.GarageSectorResponse("A", BigDecimal.TEN, 1)),
						List.of(new GarageResponse.GarageSpotResponse(1L, "A", null, BigDecimal.TEN))
				));

		assertThatThrownBy(service::run)
				.isInstanceOf(BusinessException.class)
				.satisfies(ex -> {
					BusinessException businessException = (BusinessException) ex;
					assertThat(businessException.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
					assertThat(businessException.getMessage()).isEqualTo("Garage spot coordinates are required");
				});
	}

	private EntityManager entityManager() {
		EntityManager entityManager = mock(EntityManager.class);
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery(anyString())).thenReturn(query);
		when(query.executeUpdate()).thenReturn(0);
		return entityManager;
	}
}

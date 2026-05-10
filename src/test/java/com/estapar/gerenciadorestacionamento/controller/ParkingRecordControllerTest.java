package com.estapar.gerenciadorestacionamento.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.estapar.gerenciadorestacionamento.domain.EventType;
import com.estapar.gerenciadorestacionamento.dto.WebhookRecordResponse;
import com.estapar.gerenciadorestacionamento.service.WebhookService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ParkingRecordController.class)
class ParkingRecordControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private WebhookService webhookService;

	@Test
	void listsRecordsCreatedByWebhook() throws Exception {
		when(webhookService.listRecords()).thenReturn(List.of(new WebhookRecordResponse(
				1L,
				"ZUL0001",
				Instant.parse("2025-01-01T12:00:00Z"),
				Instant.parse("2025-01-01T13:00:00Z"),
				"A",
				10L,
				BigDecimal.valueOf(-23.561684),
				BigDecimal.valueOf(-46.655981),
				BigDecimal.valueOf(10).setScale(2),
				BigDecimal.valueOf(10).setScale(2),
				EventType.EXIT
		)));

		mockMvc.perform(get("/parking-records"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].licensePlate").value("ZUL0001"))
				.andExpect(jsonPath("$[0].sector").value("A"))
				.andExpect(jsonPath("$[0].spotId").value(10))
				.andExpect(jsonPath("$[0].lastEventType").value("EXIT"));
	}
}

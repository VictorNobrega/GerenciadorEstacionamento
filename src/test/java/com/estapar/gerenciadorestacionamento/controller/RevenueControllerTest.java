package com.estapar.gerenciadorestacionamento.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.estapar.gerenciadorestacionamento.dto.RevenueResponse;
import com.estapar.gerenciadorestacionamento.service.RevenueService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RevenueController.class)
class RevenueControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RevenueService revenueService;

	@Test
	void returnsRevenueByRequestBody() throws Exception {
		when(revenueService.getRevenue(eq(LocalDate.parse("2025-01-01")), eq("A")))
				.thenReturn(new RevenueResponse(BigDecimal.valueOf(20).setScale(2), "BRL", Instant.parse("2025-01-01T15:00:00Z")));

		mockMvc.perform(get("/revenue")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "date": "2025-01-01",
								  "sector": "A"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.amount").value(20.00))
				.andExpect(jsonPath("$.currency").value("BRL"))
				.andExpect(jsonPath("$.timestamp").value("2025-01-01T15:00:00Z"));
	}
}

package com.estapar.gerenciadorestacionamento.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.estapar.gerenciadorestacionamento.service.BusinessException;
import com.estapar.gerenciadorestacionamento.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WebhookController.class)
class WebhookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private WebhookService webhookService;

	@Test
	void receivesEntryEvent() throws Exception {
		mockMvc.perform(post("/webhook")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "license_plate": "ZUL0001",
								  "entry_time": "2025-01-01T12:00:00Z",
								  "event_type": "ENTRY"
								}
								"""))
				.andExpect(status().isOk());
	}

	@Test
	void returnsConflictForBusinessErrors() throws Exception {
		doThrow(new BusinessException(HttpStatus.CONFLICT, "Sector is full")).when(webhookService).handle(any());

		mockMvc.perform(post("/webhook")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "license_plate": "ZUL0001",
								  "lat": -23.561684,
								  "lng": -46.655981,
								  "event_type": "PARKED"
								}
								"""))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("Sector is full"));
	}
}

package com.estapar.gerenciadorestacionamento.controller;

import com.estapar.gerenciadorestacionamento.dto.WebhookRequest;
import com.estapar.gerenciadorestacionamento.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Webhook")
public class WebhookController {

	private final WebhookService webhookService;

	public WebhookController(WebhookService webhookService) {
		this.webhookService = webhookService;
	}

	@PostMapping("/webhook")
	@Operation(summary = "Receives vehicle events from the garage simulator")
	public ResponseEntity<Void> receive(@Valid @RequestBody WebhookRequest request) {
		webhookService.handle(request);
		return ResponseEntity.ok().build();
	}
}

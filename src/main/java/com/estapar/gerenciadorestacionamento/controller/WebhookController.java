package com.estapar.gerenciadorestacionamento.controller;

import com.estapar.gerenciadorestacionamento.dto.WebhookRequest;
import com.estapar.gerenciadorestacionamento.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Webhook")
public class WebhookController {

	private final WebhookService webhookService;

	public WebhookController(WebhookService webhookService) {
		this.webhookService = webhookService;
	}

	@PostMapping("/webhook")
	@Operation(
			summary = "Recebe eventos do simulador",
			description = "Processa eventos ENTRY, PARKED e EXIT enviados pelo simulador para criar e atualizar permanencias de veiculos."
	)
	@RequestBody(
			required = true,
			description = "Evento enviado pelo simulador. Campos obrigatorios variam conforme o event_type.",
			content = @Content(
					schema = @Schema(implementation = WebhookRequest.class),
					examples = {
							@ExampleObject(
									name = "ENTRY",
									value = """
											{
											  "license_plate": "ZUL0001",
											  "entry_time": "2026-05-10T10:00:00Z",
											  "event_type": "ENTRY"
											}
											"""
							),
							@ExampleObject(
									name = "PARKED",
									value = """
											{
											  "license_plate": "ZUL0001",
											  "lat": -23.561684,
											  "lng": -46.655981,
											  "event_type": "PARKED"
											}
											"""
							),
							@ExampleObject(
									name = "EXIT",
									value = """
											{
											  "license_plate": "ZUL0001",
											  "exit_time": "2026-05-10T10:40:00Z",
											  "event_type": "EXIT"
											}
											"""
							)
					}
			)
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Evento processado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Payload invalido ou campo obrigatorio ausente"),
			@ApiResponse(responseCode = "404", description = "Entrada ativa ou vaga nao encontrada"),
			@ApiResponse(responseCode = "409", description = "Vaga ocupada ou setor sem disponibilidade")
	})
	public ResponseEntity<Void> receive(@Valid @org.springframework.web.bind.annotation.RequestBody WebhookRequest request) {
		webhookService.handle(request);
		return ResponseEntity.ok().build();
	}
}

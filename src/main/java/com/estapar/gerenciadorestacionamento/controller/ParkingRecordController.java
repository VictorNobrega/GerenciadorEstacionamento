package com.estapar.gerenciadorestacionamento.controller;

import com.estapar.gerenciadorestacionamento.dto.WebhookRecordResponse;
import com.estapar.gerenciadorestacionamento.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Parking Records")
public class ParkingRecordController {

	private final WebhookService webhookService;

	public ParkingRecordController(WebhookService webhookService) {
		this.webhookService = webhookService;
	}

	@GetMapping("/parking-records")
	@Operation(
			summary = "Lista registros de estacionamento",
			description = "Retorna as permanencias criadas e atualizadas pelos eventos recebidos no webhook, ordenadas das entradas mais recentes para as mais antigas."
	)
	@ApiResponse(
			responseCode = "200",
			description = "Registros encontrados",
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = WebhookRecordResponse.class)))
	)
	public List<WebhookRecordResponse> listRecords() {
		return webhookService.listRecords();
	}
}

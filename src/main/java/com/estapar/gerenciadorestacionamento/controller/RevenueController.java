package com.estapar.gerenciadorestacionamento.controller;

import com.estapar.gerenciadorestacionamento.dto.RevenueRequest;
import com.estapar.gerenciadorestacionamento.dto.RevenueResponse;
import com.estapar.gerenciadorestacionamento.service.RevenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Revenue")
public class RevenueController {

	private final RevenueService revenueService;

	public RevenueController(RevenueService revenueService) {
		this.revenueService = revenueService;
	}

	@GetMapping("/revenue")
	@Operation(
			summary = "Consulta faturamento por data e setor",
			description = "Soma o valor das permanencias encerradas no dia informado para o setor informado. O contrato do desafio usa body em GET; clientes como Postman e curl funcionam, mas o Try it out do Swagger UI pode falhar por limitacao do navegador."
	)
	@RequestBody(
			required = true,
			description = "Data e setor usados para filtrar o faturamento.",
			content = @Content(
					schema = @Schema(implementation = RevenueRequest.class),
					examples = @ExampleObject(
							name = "Faturamento setor A",
							value = """
									{
									  "date": "2026-05-10",
									  "sector": "A"
									}
									"""
					)
			)
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Faturamento calculado",
					content = @Content(schema = @Schema(implementation = RevenueResponse.class))
			),
			@ApiResponse(responseCode = "400", description = "Payload invalido"),
			@ApiResponse(responseCode = "404", description = "Setor nao encontrado")
	})
	public RevenueResponse getRevenue(@Valid @org.springframework.web.bind.annotation.RequestBody RevenueRequest request) {
		return revenueService.getRevenue(request.date(), request.sector());
	}
}

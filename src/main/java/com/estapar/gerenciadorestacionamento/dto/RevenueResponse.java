package com.estapar.gerenciadorestacionamento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Resultado da consulta de faturamento")
public record RevenueResponse(
		@Schema(description = "Valor total faturado", example = "36.45")
		BigDecimal amount,
		@Schema(description = "Moeda do faturamento", example = "BRL")
		String currency,
		@Schema(description = "Horario da resposta", example = "2026-05-10T21:00:44.400617258Z")
		Instant timestamp
) {
}

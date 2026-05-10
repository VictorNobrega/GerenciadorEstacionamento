package com.estapar.gerenciadorestacionamento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Resposta padrao de erro")
public record ErrorResponse(
		@Schema(description = "Horario do erro", example = "2026-05-10T21:00:44.400617258Z")
		Instant timestamp,
		@Schema(description = "Status HTTP", example = "400")
		int status,
		@Schema(description = "Descricao do status HTTP", example = "Bad Request")
		String error,
		@Schema(description = "Mensagem de erro", example = "entry_time is required for ENTRY events")
		String message
) {
}

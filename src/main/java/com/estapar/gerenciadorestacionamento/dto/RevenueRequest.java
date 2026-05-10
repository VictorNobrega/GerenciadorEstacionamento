package com.estapar.gerenciadorestacionamento.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "Filtro para consulta de faturamento")
public record RevenueRequest(
		@Schema(description = "Data de fechamento das permanencias", example = "2026-05-10", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull LocalDate date,
		@Schema(description = "Codigo do setor", example = "A", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank String sector
) {
}

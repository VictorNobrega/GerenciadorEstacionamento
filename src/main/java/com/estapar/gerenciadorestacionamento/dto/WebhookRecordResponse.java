package com.estapar.gerenciadorestacionamento.dto;

import com.estapar.gerenciadorestacionamento.domain.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Permanencia criada a partir dos eventos do webhook")
public record WebhookRecordResponse(
		@Schema(description = "Identificador da permanencia", example = "1")
		Long id,
		@Schema(description = "Placa do veiculo", example = "ZUL0001")
		String licensePlate,
		@Schema(description = "Horario de entrada", example = "2026-05-10T10:00:00Z")
		Instant entryTime,
		@Schema(description = "Horario de saida, quando a permanencia ja foi encerrada", example = "2026-05-10T10:40:00Z")
		Instant exitTime,
		@Schema(description = "Setor onde o veiculo estacionou", example = "A")
		String sector,
		@Schema(description = "Identificador da vaga", example = "1")
		Long spotId,
		@Schema(description = "Latitude da vaga", example = "-23.561684")
		BigDecimal lat,
		@Schema(description = "Longitude da vaga", example = "-46.655981")
		BigDecimal lng,
		@Schema(description = "Preco por hora definido no momento do estacionamento", example = "36.45")
		BigDecimal hourlyPrice,
		@Schema(description = "Valor cobrado ao encerrar a permanencia", example = "36.45")
		BigDecimal amount,
		@Schema(description = "Ultimo evento aplicado a permanencia", example = "EXIT")
		EventType lastEventType
) {
}

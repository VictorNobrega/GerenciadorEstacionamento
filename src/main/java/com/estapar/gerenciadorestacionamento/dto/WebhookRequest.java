package com.estapar.gerenciadorestacionamento.dto;

import com.estapar.gerenciadorestacionamento.enums.EventType;
import com.estapar.gerenciadorestacionamento.config.FlexibleInstantDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Evento recebido do simulador de garagem")
public record WebhookRequest(
		@Schema(description = "Placa do veiculo", example = "ZUL0001", requiredMode = Schema.RequiredMode.REQUIRED)
		@JsonProperty("license_plate") @NotBlank String licensePlate,
		@Schema(description = "Horario de entrada. Obrigatorio para ENTRY.", example = "2026-05-10T10:00:00Z")
		@JsonProperty("entry_time") @JsonDeserialize(using = FlexibleInstantDeserializer.class) Instant entryTime,
		@Schema(description = "Horario de saida. Obrigatorio para EXIT.", example = "2026-05-10T10:40:00Z")
		@JsonProperty("exit_time") @JsonDeserialize(using = FlexibleInstantDeserializer.class) Instant exitTime,
		@Schema(description = "Latitude da vaga. Obrigatoria para PARKED.", example = "-23.561684")
		BigDecimal lat,
		@Schema(description = "Longitude da vaga. Obrigatoria para PARKED.", example = "-46.655981")
		BigDecimal lng,
		@Schema(description = "Tipo do evento recebido", example = "ENTRY", requiredMode = Schema.RequiredMode.REQUIRED)
		@JsonProperty("event_type") @NotNull EventType eventType
) {
}

package com.estapar.gerenciadorestacionamento.dto;

import com.estapar.gerenciadorestacionamento.domain.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public record WebhookRequest(
		@JsonProperty("license_plate") @NotBlank String licensePlate,
		@JsonProperty("entry_time") Instant entryTime,
		@JsonProperty("exit_time") Instant exitTime,
		BigDecimal lat,
		BigDecimal lng,
		@JsonProperty("event_type") @NotNull EventType eventType
) {
}

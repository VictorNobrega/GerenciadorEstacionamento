package com.estapar.gerenciadorestacionamento.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record GarageResponse(
		@NotEmpty List<@Valid GarageSectorResponse> garage,
		@NotEmpty List<@Valid GarageSpotResponse> spots
) {

	public record GarageSectorResponse(
			@NotBlank String sector,
			@JsonAlias("base_price") @NotNull @Positive BigDecimal basePrice,
			@JsonProperty("max_capacity") @Positive int maxCapacity
	) {
	}

	public record GarageSpotResponse(
			@NotNull Long id,
			@NotBlank String sector,
			@NotNull BigDecimal lat,
			@NotNull BigDecimal lng
	) {
	}
}

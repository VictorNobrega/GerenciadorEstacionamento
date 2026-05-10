package com.estapar.gerenciadorestacionamento.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.estapar.gerenciadorestacionamento.domain.Sector;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class PricingServiceTest {

	private final PricingService pricingService = new PricingService();

	@Test
	void appliesDynamicPriceByOccupancyRange() {
		Sector sector = new Sector("A", BigDecimal.valueOf(10), 100);

		assertThat(pricingService.dynamicHourlyPrice(sector, 24)).isEqualByComparingTo("9.00");
		assertThat(pricingService.dynamicHourlyPrice(sector, 25)).isEqualByComparingTo("10.00");
		assertThat(pricingService.dynamicHourlyPrice(sector, 51)).isEqualByComparingTo("11.00");
		assertThat(pricingService.dynamicHourlyPrice(sector, 76)).isEqualByComparingTo("12.50");
	}

	@Test
	void calculatesFreeAndRoundedUpPeriods() {
		Instant entry = Instant.parse("2025-01-01T12:00:00Z");
		BigDecimal hourlyPrice = BigDecimal.valueOf(10);

		assertThat(pricingService.calculateAmount(entry, entry.plusSeconds(30 * 60), hourlyPrice)).isEqualByComparingTo("0.00");
		assertThat(pricingService.calculateAmount(entry, entry.plusSeconds(31 * 60), hourlyPrice)).isEqualByComparingTo("10.00");
		assertThat(pricingService.calculateAmount(entry, entry.plusSeconds(61 * 60), hourlyPrice)).isEqualByComparingTo("20.00");
	}
}

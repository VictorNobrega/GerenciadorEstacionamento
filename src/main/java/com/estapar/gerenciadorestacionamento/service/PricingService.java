package com.estapar.gerenciadorestacionamento.service;

import com.estapar.gerenciadorestacionamento.domain.Sector;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

	private static final BigDecimal DISCOUNT_10 = BigDecimal.valueOf(0.90);
	private static final BigDecimal INCREASE_10 = BigDecimal.valueOf(1.10);
	private static final BigDecimal INCREASE_25 = BigDecimal.valueOf(1.25);

	public BigDecimal dynamicHourlyPrice(Sector sector, long occupiedBeforeParking) {
		BigDecimal occupancy = BigDecimal.valueOf(occupiedBeforeParking)
				.divide(BigDecimal.valueOf(sector.getMaxCapacity()), 4, RoundingMode.HALF_UP)
				.multiply(BigDecimal.valueOf(100));

		BigDecimal multiplier;
		if (occupancy.compareTo(BigDecimal.valueOf(25)) < 0) {
			multiplier = DISCOUNT_10;
		} else if (occupancy.compareTo(BigDecimal.valueOf(50)) <= 0) {
			multiplier = BigDecimal.ONE;
		} else if (occupancy.compareTo(BigDecimal.valueOf(75)) <= 0) {
			multiplier = INCREASE_10;
		} else {
			multiplier = INCREASE_25;
		}

		return sector.getBasePrice().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
	}

	public BigDecimal calculateAmount(Instant entryTime, Instant exitTime, BigDecimal hourlyPrice) {
		long parkedMinutes = Math.max(0, Duration.between(entryTime, exitTime).toMinutes());
		if (parkedMinutes <= 30) {
			return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		}

		long hours = (long) Math.ceil(parkedMinutes / 60.0);
		return hourlyPrice.multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP);
	}
}

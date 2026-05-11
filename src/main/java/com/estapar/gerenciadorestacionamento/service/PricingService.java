package com.estapar.gerenciadorestacionamento.service;

import com.estapar.gerenciadorestacionamento.domain.Sector;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

	private static final int OCCUPANCY_DIVISION_SCALE = 4;
	private static final int MONEY_SCALE = 2;
	private static final long FREE_PARKING_MINUTES = 30;
	private static final double MINUTES_PER_HOUR = 60.0;
	private static final BigDecimal PERCENT = BigDecimal.valueOf(100);
	private static final BigDecimal LOW_OCCUPANCY_THRESHOLD = BigDecimal.valueOf(25);
	private static final BigDecimal MEDIUM_OCCUPANCY_THRESHOLD = BigDecimal.valueOf(50);
	private static final BigDecimal HIGH_OCCUPANCY_THRESHOLD = BigDecimal.valueOf(75);
	private static final BigDecimal DISCOUNT_10 = BigDecimal.valueOf(0.90);
	private static final BigDecimal INCREASE_10 = BigDecimal.valueOf(1.10);
	private static final BigDecimal INCREASE_25 = BigDecimal.valueOf(1.25);

	public BigDecimal dynamicHourlyPrice(Sector sector, long occupiedBeforeParking) {
		BigDecimal occupancy = BigDecimal.valueOf(occupiedBeforeParking)
				.divide(BigDecimal.valueOf(sector.getMaxCapacity()), OCCUPANCY_DIVISION_SCALE, RoundingMode.HALF_UP)
				.multiply(PERCENT);

		BigDecimal multiplier;
		if (occupancy.compareTo(LOW_OCCUPANCY_THRESHOLD) < 0) {
			multiplier = DISCOUNT_10;
		} else if (occupancy.compareTo(MEDIUM_OCCUPANCY_THRESHOLD) <= 0) {
			multiplier = BigDecimal.ONE;
		} else if (occupancy.compareTo(HIGH_OCCUPANCY_THRESHOLD) <= 0) {
			multiplier = INCREASE_10;
		} else {
			multiplier = INCREASE_25;
		}

		return sector.getBasePrice().multiply(multiplier).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
	}

	public BigDecimal calculateAmount(Instant entryTime, Instant exitTime, BigDecimal hourlyPrice) {
		long parkedMinutes = Math.max(0, Duration.between(entryTime, exitTime).toMinutes());
		if (parkedMinutes <= FREE_PARKING_MINUTES) {
			return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
		}

		long hours = (long) Math.ceil(parkedMinutes / MINUTES_PER_HOUR);
		return hourlyPrice.multiply(BigDecimal.valueOf(hours)).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
	}
}

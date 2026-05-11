package com.estapar.gerenciadorestacionamento.service;

import com.estapar.gerenciadorestacionamento.dto.RevenueResponse;
import com.estapar.gerenciadorestacionamento.exception.BusinessException;
import com.estapar.gerenciadorestacionamento.repository.SectorRepository;
import com.estapar.gerenciadorestacionamento.repository.VehicleStayRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RevenueService {

	private static final int MONEY_SCALE = 2;
	private static final String CURRENCY_CODE = "BRL";

	private final VehicleStayRepository vehicleStayRepository;
	private final SectorRepository sectorRepository;

	public RevenueService(VehicleStayRepository vehicleStayRepository, SectorRepository sectorRepository) {
		this.vehicleStayRepository = vehicleStayRepository;
		this.sectorRepository = sectorRepository;
	}

	public RevenueResponse getRevenue(LocalDate date, String sector) {
		if (!sectorRepository.existsById(sector)) {
			throw new BusinessException(HttpStatus.NOT_FOUND, "Sector not found");
		}
		Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant end = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
		BigDecimal amount = vehicleStayRepository.sumRevenue(sector, start, end).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
		return new RevenueResponse(amount, CURRENCY_CODE, Instant.now());
	}
}

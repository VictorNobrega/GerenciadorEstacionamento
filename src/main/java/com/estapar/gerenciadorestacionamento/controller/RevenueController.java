package com.estapar.gerenciadorestacionamento.controller;

import com.estapar.gerenciadorestacionamento.dto.RevenueResponse;
import com.estapar.gerenciadorestacionamento.service.RevenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Revenue")
public class RevenueController {

	private final RevenueService revenueService;

	public RevenueController(RevenueService revenueService) {
		this.revenueService = revenueService;
	}

	@GetMapping("/revenue")
	@Operation(summary = "Returns total revenue by date and sector")
	public RevenueResponse getRevenue(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam String sector
	) {
		return revenueService.getRevenue(date, sector);
	}
}

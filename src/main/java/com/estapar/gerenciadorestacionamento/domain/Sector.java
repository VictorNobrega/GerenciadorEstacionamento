package com.estapar.gerenciadorestacionamento.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "sectors")
public class Sector {

	@Id
	@Column(length = 30)
	private String code;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal basePrice;

	@Column(nullable = false)
	private int maxCapacity;

	protected Sector() {
	}

	public Sector(String code, BigDecimal basePrice, int maxCapacity) {
		this.code = code;
		this.basePrice = basePrice;
		this.maxCapacity = maxCapacity;
	}

	public String getCode() {
		return code;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void update(BigDecimal basePrice, int maxCapacity) {
		this.basePrice = basePrice;
		this.maxCapacity = maxCapacity;
	}
}

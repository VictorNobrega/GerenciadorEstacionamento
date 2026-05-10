package com.estapar.gerenciadorestacionamento.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "parking_spots")
public class ParkingSpot {

	@Id
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "sector_code", nullable = false)
	private Sector sector;

	@Column(nullable = false, precision = 10, scale = 6)
	private BigDecimal lat;

	@Column(nullable = false, precision = 10, scale = 6)
	private BigDecimal lng;

	@Column(nullable = false)
	private boolean occupied;

	protected ParkingSpot() {
	}

	public ParkingSpot(Long id, Sector sector, BigDecimal lat, BigDecimal lng) {
		this.id = id;
		this.sector = sector;
		this.lat = lat;
		this.lng = lng;
	}

	public Long getId() {
		return id;
	}

	public Sector getSector() {
		return sector;
	}

	public BigDecimal getLat() {
		return lat;
	}

	public BigDecimal getLng() {
		return lng;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void update(Sector sector, BigDecimal lat, BigDecimal lng) {
		this.sector = sector;
		this.lat = lat;
		this.lng = lng;
	}

	public void occupy() {
		this.occupied = true;
	}

	public void release() {
		this.occupied = false;
	}
}

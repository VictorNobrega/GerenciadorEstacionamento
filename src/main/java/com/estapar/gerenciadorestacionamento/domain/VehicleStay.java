package com.estapar.gerenciadorestacionamento.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "vehicle_stays")
public class VehicleStay {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 20)
	private String licensePlate;

	@Column(nullable = false)
	private Instant entryTime;

	private Instant exitTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sector_code")
	private Sector sector;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "spot_id")
	private ParkingSpot spot;

	@Column(precision = 10, scale = 2)
	private BigDecimal hourlyPrice;

	@Column(precision = 10, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EventType lastEventType;

	protected VehicleStay() {
	}

	public VehicleStay(String licensePlate, Instant entryTime) {
		this.licensePlate = licensePlate;
		this.entryTime = entryTime;
		this.lastEventType = EventType.ENTRY;
	}

	public Long getId() {
		return id;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public Instant getEntryTime() {
		return entryTime;
	}

	public Instant getExitTime() {
		return exitTime;
	}

	public Sector getSector() {
		return sector;
	}

	public ParkingSpot getSpot() {
		return spot;
	}

	public BigDecimal getHourlyPrice() {
		return hourlyPrice;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public EventType getLastEventType() {
		return lastEventType;
	}

	public boolean isClosed() {
		return exitTime != null;
	}

	public void refreshEntryTime(Instant entryTime) {
		this.entryTime = entryTime;
		this.lastEventType = EventType.ENTRY;
	}

	public void park(ParkingSpot spot, BigDecimal hourlyPrice) {
		this.spot = spot;
		this.sector = spot.getSector();
		this.hourlyPrice = hourlyPrice;
		this.lastEventType = EventType.PARKED;
	}

	public void close(Instant exitTime, BigDecimal amount) {
		this.exitTime = exitTime;
		this.amount = amount;
		this.lastEventType = EventType.EXIT;
	}
}

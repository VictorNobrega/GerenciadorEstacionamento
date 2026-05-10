package com.estapar.gerenciadorestacionamento.repository;

import com.estapar.gerenciadorestacionamento.domain.VehicleStay;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleStayRepository extends JpaRepository<VehicleStay, Long> {

	Optional<VehicleStay> findFirstByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(String licensePlate);

	@EntityGraph(attributePaths = {"sector", "spot"})
	List<VehicleStay> findAllByOrderByEntryTimeDescIdDesc();

	@Query("""
			select coalesce(sum(v.amount), 0)
			from VehicleStay v
			where v.sector.code = :sector
			  and v.exitTime >= :start
			  and v.exitTime < :end
			  and v.amount is not null
			""")
	BigDecimal sumRevenue(@Param("sector") String sector, @Param("start") Instant start, @Param("end") Instant end);
}

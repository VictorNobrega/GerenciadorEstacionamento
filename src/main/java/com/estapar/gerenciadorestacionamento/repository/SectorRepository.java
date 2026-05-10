package com.estapar.gerenciadorestacionamento.repository;

import com.estapar.gerenciadorestacionamento.domain.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepository extends JpaRepository<Sector, String> {
}

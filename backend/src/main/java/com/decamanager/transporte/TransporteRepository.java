package com.decamanager.transporte;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransporteRepository extends JpaRepository<Transporte, Long> {

    @Query("""
            SELECT t FROM Transporte t
            WHERE (:estado IS NULL OR t.estado = :estado)
              AND (:fechaDesde IS NULL OR t.fechaOperacion >= :fechaDesde)
              AND (:fechaHasta IS NULL OR t.fechaOperacion <= :fechaHasta)
            ORDER BY t.fechaOperacion DESC, t.id DESC
            """)
    List<Transporte> buscar(
            @Param("estado") EstadoTransporte estado,
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta);
}
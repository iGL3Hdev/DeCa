package com.decamanager.deca;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoDecaRepository extends JpaRepository<DocumentoDeca, Long> {

    Optional<DocumentoDeca> findByUrlPublica(String urlPublica);

    Optional<DocumentoDeca> findByTransporteId(Long transporteId);
}

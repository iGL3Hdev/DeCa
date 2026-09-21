package com.decamanager.vehiculo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    List<Vehiculo> findByActivo(boolean activo);

    boolean existsByMatricula(String matricula);
    
    
}

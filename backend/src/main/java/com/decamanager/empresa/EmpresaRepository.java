package com.decamanager.empresa;

import org.springframework.data.jpa.repository.JpaRepository;




public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    boolean existsByNif(String nif);

    boolean existsByNifAndIdNot(String nif, Long id);
    
}

package com.decamanager.vehiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VehiculoRequest (

    @NotBlank @Size(max = 15) String matricula,
    @NotBlank @Size (max = 50) String tipo,
    @Size(max = 15) String matriculaRemolque
    
) {
    
}

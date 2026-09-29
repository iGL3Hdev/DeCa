package com.decamanager.transporte;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@FechasCoherentes
public record TransporteRequest (

    @NotNull Long cargadorId,
    @NotNull Long transportistaId,
    @NotNull Long vehiculoId,
    @NotNull LocalDate fechaOperacion,
    @NotBlank @Size(max = 255) String lugarCarga,
    @NotNull LocalDate fechaCarga,
    @NotBlank @Size(max = 255) String lugarDescarga,
    @NotNull LocalDate fechaDescarga,
    @NotBlank @Size(max = 150) String mercanciaNaturaleza,
    @NotNull @DecimalMin(value = "0.01") BigDecimal mercanciaPeso,
    @NotBlank @Size(max = 10) String mercanciaUnidad,
    String notas
    
) {

}
 
package com.decamanager.empresa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmpresaRequest(
        @NotBlank @Size(max = 150) String nombre,
        @NotBlank @Size(max = 20) String nif,
        @Size(max = 255) String domicilio,
        @Size(max = 30) String telefono
) {
}


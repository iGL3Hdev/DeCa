package com.decamanager.empresa;

public record EmpresaResponse(
        Long id,
        String nombre,
        String nif,
        String domicilio,
        String telefono
) {

    public static EmpresaResponse from(Empresa e) {
        return new EmpresaResponse(
                e.getId(),
                e.getNombre(),
                e.getNif(),
                e.getDomicilio(),
                e.getTelefono()
        );
    }
}
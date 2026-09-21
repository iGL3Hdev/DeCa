package com.decamanager.vehiculo;

public record VehiculoResponse (
    
    Long id,
    String matricula,
    String tipo,
    String matriculaRemolque,
    boolean activo
) {

    public static VehiculoResponse from(Vehiculo v) {
        return new VehiculoResponse(
            v.getId(),
            v.getMatricula(),
            v.getTipo(),
            v.getMatriculaRemolque(),
            v.isActivo()
        );
    }
    
}

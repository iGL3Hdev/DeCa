package com.decamanager.transporte;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransporteResponse(
        Long id,
        EmpresaResumen cargador,
        EmpresaResumen transportista,
        VehiculoResumen vehiculo,
        LocalDate fechaOperacion,
        String lugarCarga,
        LocalDate fechaCarga,
        String lugarDescarga,
        LocalDate fechaDescarga,
        String mercanciaNaturaleza,
        BigDecimal mercanciaPeso,
        String mercanciaUnidad,
        String notas,
        EstadoTransporte estado
) {

    public static TransporteResponse from(Transporte t) {
        return new TransporteResponse(
                t.getId(),
                EmpresaResumen.from(t.getCargador()),
                EmpresaResumen.from(t.getTransportista()),
                VehiculoResumen.from(t.getVehiculo()),
                t.getFechaOperacion(),
                t.getLugarCarga(),
                t.getFechaCarga(),
                t.getLugarDescarga(),
                t.getFechaDescarga(),
                t.getMercanciaNaturaleza(),
                t.getMercanciaPeso(),
                t.getMercanciaUnidad(),
                t.getNotas(),
                t.getEstado());
    }

    public record EmpresaResumen(Long id, String nombre, String nif) {
        public static EmpresaResumen from(com.decamanager.empresa.Empresa e) {
            return new EmpresaResumen(e.getId(), e.getNombre(), e.getNif());
        }
    }

    public record VehiculoResumen(Long id, String matricula, String tipo) {
        public static VehiculoResumen from(com.decamanager.vehiculo.Vehiculo v) {
            return new VehiculoResumen(v.getId(), v.getMatricula(), v.getTipo());
        }
    }
}


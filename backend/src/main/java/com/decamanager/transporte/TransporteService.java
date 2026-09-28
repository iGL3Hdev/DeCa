package com.decamanager.transporte;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.decamanager.common.ConflictoException;
import com.decamanager.common.RecursoNoEncontradoException;
import com.decamanager.empresa.Empresa;
import com.decamanager.empresa.EmpresaRepository;
import com.decamanager.vehiculo.Vehiculo;
import com.decamanager.vehiculo.VehiculoRepository;

@Service
@Transactional(readOnly = true)
public class TransporteService {

    private final TransporteRepository repository;
    private final EmpresaRepository empresaRepository;
    private final VehiculoRepository vehiculoRepository;

    public TransporteService(TransporteRepository repository,
            EmpresaRepository empresaRepository,
            VehiculoRepository vehiculoRepository) {
        this.repository = repository;
        this.empresaRepository = empresaRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<TransporteResponse> buscar(EstadoTransporte estado, LocalDate fechaDesde, LocalDate fechaHasta) {
        return repository.buscar(estado, fechaDesde, fechaHasta).stream()
                .map(TransporteResponse::from)
                .toList();
    }

    public TransporteResponse obtener(Long id) {
        return TransporteResponse.from(buscar(id));
    }

    @Transactional
    public TransporteResponse crear(TransporteRequest request) {
        Empresa cargador = buscarEmpresa(request.cargadorId());
        Empresa transportista = buscarEmpresa(request.transportistaId());
        Vehiculo vehiculo = buscarVehiculo(request.vehiculoId());

        Transporte transporte = new Transporte(
                cargador, transportista, vehiculo,
                request.fechaOperacion(), request.lugarCarga(), request.fechaCarga(),
                request.lugarDescarga(), request.fechaDescarga(),
                request.mercanciaNaturaleza().trim(), request.mercanciaPeso(),
                request.mercanciaUnidad().trim().toUpperCase(),
                limpiarOpcional(request.notas()));

        return TransporteResponse.from(repository.save(transporte));
    }

    @Transactional
    public TransporteResponse actualizar(Long id, TransporteRequest request) {
        Transporte transporte = buscar(id);
        if (transporte.getEstado() != EstadoTransporte.BORRADOR) {
            throw new ConflictoException(
                    "No se puede editar un transporte con estado " + transporte.getEstado());
        }

        transporte.setCargador(buscarEmpresa(request.cargadorId()));
        transporte.setTransportista(buscarEmpresa(request.transportistaId()));
        transporte.setVehiculo(buscarVehiculo(request.vehiculoId()));
        transporte.setFechaOperacion(request.fechaOperacion());
        transporte.setLugarCarga(request.lugarCarga());
        transporte.setFechaCarga(request.fechaCarga());
        transporte.setLugarDescarga(request.lugarDescarga());
        transporte.setFechaDescarga(request.fechaDescarga());
        transporte.setMercanciaNaturaleza(request.mercanciaNaturaleza().trim());
        transporte.setMercanciaPeso(request.mercanciaPeso());
        transporte.setMercanciaUnidad(request.mercanciaUnidad().trim().toUpperCase());
        transporte.setNotas(limpiarOpcional(request.notas()));

        return TransporteResponse.from(transporte);
    }

    private Transporte buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el transporte con id " + id));
    }

    private Empresa buscarEmpresa(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la empresa con id " + id));
    }

    private Vehiculo buscarVehiculo(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el vehículo con id " + id));
    }

    private String limpiarOpcional(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}

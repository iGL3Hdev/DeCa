package com.decamanager.vehiculo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.decamanager.common.ConflictoException;
import com.decamanager.common.RecursoNoEncontradoException;

@Service
@Transactional(readOnly = true)

public class VehiculoService {

    private final VehiculoRepository repository;

    public VehiculoService(VehiculoRepository repository) {
        this.repository = repository;
    }

    public List<VehiculoResponse> listar(Boolean activo) {
        List<Vehiculo> vehiculos = (activo == null)
                ? repository.findAll()
                : repository.findByActivo(activo);
        return vehiculos.stream().map(VehiculoResponse::from).toList();
    }

    public VehiculoResponse obtener(Long id) {
        return VehiculoResponse.from(buscar(id));
    }

    @Transactional
    public VehiculoResponse crear(VehiculoRequest request) {
        String matricula = normalizarMatricula(request.matricula());
        if (repository.existsByMatricula(matricula)) {
            throw new ConflictoException("Ya existe un vehículo con la matrícula " + matricula);
        }
        Vehiculo vehiculo = new Vehiculo(
                matricula,
                request.tipo().trim(),
                normalizarOpcional(request.matriculaRemolque()));
        return VehiculoResponse.from(repository.save(vehiculo));
    }

    @Transactional
    public VehiculoResponse actualizar(Long id, VehiculoRequest request) {
        Vehiculo vehiculo = buscar(id);
        String matricula = normalizarMatricula(request.matricula());
        if (!matricula.equals(vehiculo.getMatricula()) && repository.existsByMatricula(matricula)) {
            throw new ConflictoException("Ya existe un vehículo con la matrícula " + matricula);
        }
        vehiculo.setMatricula(matricula);
        vehiculo.setTipo(request.tipo().trim());
        vehiculo.setMatriculaRemolque(normalizarOpcional(request.matriculaRemolque()));
        return VehiculoResponse.from(vehiculo);
    }

    @Transactional
    public VehiculoResponse darDeBaja(Long id) {
        Vehiculo vehiculo = buscar(id);
        vehiculo.setActivo(false);
        return VehiculoResponse.from(vehiculo);
    }

    private Vehiculo buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el vehículo con id " + id));
    }

    private String normalizarMatricula(String matricula) {
        return matricula.trim().toUpperCase();
    }

    private String normalizarOpcional(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim().toUpperCase();
    }
    
}

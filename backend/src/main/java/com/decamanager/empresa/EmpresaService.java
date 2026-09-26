package com.decamanager.empresa;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.decamanager.common.ConflictoException;
import com.decamanager.common.RecursoNoEncontradoException;

@Service
@Transactional(readOnly = true)
public class EmpresaService {

    private final EmpresaRepository repository;

    public EmpresaService(EmpresaRepository repository) {
        this.repository = repository;
    }

    public List<EmpresaResponse> listar() {
        return repository.findAll().stream().map(EmpresaResponse::from).toList();
    }

    public EmpresaResponse obtener(Long id) {
        return EmpresaResponse.from(buscar(id));
    }

    @Transactional
    public EmpresaResponse crear(EmpresaRequest request) {
        String nif = normalizarNif(request.nif());
        if (repository.existsByNif(nif)) {
            throw new ConflictoException("Ya existe una empresa con el NIF " + nif);
        }
        Empresa empresa = new Empresa(
                request.nombre().trim(),
                nif,
                limpiarOpcional(request.domicilio()),
                limpiarOpcional(request.telefono()));
        return EmpresaResponse.from(repository.save(empresa));
    }

    @Transactional
    public EmpresaResponse actualizar(Long id, EmpresaRequest request) {
        Empresa empresa = buscar(id);
        String nif = normalizarNif(request.nif());
        if (repository.existsByNifAndIdNot(nif, id)) {
            throw new ConflictoException("Ya existe otra empresa con el NIF " + nif);
        }
        empresa.setNombre(request.nombre().trim());
        empresa.setNif(nif);
        empresa.setDomicilio(limpiarOpcional(request.domicilio()));
        empresa.setTelefono(limpiarOpcional(request.telefono()));
        return EmpresaResponse.from(empresa);
    }

    @Transactional
    public void eliminar(Long id) {
        Empresa empresa = buscar(id);
        try {
            repository.delete(empresa);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictoException(
                    "No se puede eliminar la empresa porque tiene transportes asociados");
        }
    }

    private Empresa buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la empresa con id " + id));
    }

    private String normalizarNif(String nif) {
        return nif.replaceAll("[\\s-]", "").toUpperCase();
    }

    private String limpiarOpcional(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}


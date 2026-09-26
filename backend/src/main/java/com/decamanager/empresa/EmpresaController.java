package com.decamanager.empresa;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService service;

    public EmpresaController(EmpresaService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmpresaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public EmpresaResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaResponse crear(@Valid @RequestBody EmpresaRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public EmpresaResponse actualizar(@PathVariable Long id, @Valid @RequestBody EmpresaRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}


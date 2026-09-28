package com.decamanager.transporte;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transportes")
public class TransporteController {

    private final TransporteService service;

    public TransporteController(TransporteService service) {
        this.service = service;
    }

    @GetMapping
    public List<TransporteResponse> listar(
            @RequestParam(required = false) EstadoTransporte estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        return service.buscar(estado, fechaDesde, fechaHasta);
    }

    @GetMapping("/{id}")
    public TransporteResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransporteResponse crear(@Valid @RequestBody TransporteRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public TransporteResponse actualizar(@PathVariable Long id, @Valid @RequestBody TransporteRequest request) {
        return service.actualizar(id, request);
    }
}

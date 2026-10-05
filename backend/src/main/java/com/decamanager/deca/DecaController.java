package com.decamanager.deca;

import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DecaController {

    private final DecaService service;

    public DecaController(DecaService service) {
        this.service = service;
    }

    @PostMapping("/api/transportes/{id}/generar-deca")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoDecaResponse generar(@PathVariable Long id) {
        return service.generar(id);
    }

    @GetMapping("/api/deca/{token}")
    public ResponseEntity<byte[]> descargarPublico(@PathVariable String token) {
        byte[] pdf = service.obtenerPdfPorToken(token);

        ContentDisposition disposicion = ContentDisposition.inline()
                .filename("deca_" + token + ".pdf")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposicion.toString())
                .body(pdf);
    }

    @GetMapping("/api/documentos")
    public List<DocumentoDecaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/api/documentos/{id}")
    public DocumentoDecaResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }
}

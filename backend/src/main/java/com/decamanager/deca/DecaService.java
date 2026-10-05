package com.decamanager.deca;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.decamanager.common.ConflictoException;
import com.decamanager.common.RecursoNoEncontradoException;
import com.decamanager.transporte.EstadoTransporte;
import com.decamanager.transporte.Transporte;
import com.decamanager.transporte.TransporteRepository;

@Service
@Transactional(readOnly = true)
public class DecaService {

    private final TransporteRepository transporteRepository;
    private final DocumentoDecaRepository documentoRepository;
    private final PdfGenerator pdfGenerator;
    private final QrGenerator qrGenerator;
    private final DecaProperties propiedades;

    public DecaService(TransporteRepository transporteRepository,
            DocumentoDecaRepository documentoRepository,
            PdfGenerator pdfGenerator,
            QrGenerator qrGenerator,
            DecaProperties propiedades) {
        this.transporteRepository = transporteRepository;
        this.documentoRepository = documentoRepository;
        this.pdfGenerator = pdfGenerator;
        this.qrGenerator = qrGenerator;
        this.propiedades = propiedades;
    }

    @Transactional
    public DocumentoDecaResponse generar(Long transporteId) {
        Transporte transporte = transporteRepository.findById(transporteId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el transporte con id " + transporteId));

        if (transporte.getEstado() == EstadoTransporte.GENERADO) {
            throw new ConflictoException("El transporte ya tiene un DeCA generado");
        }

        String token = UUID.randomUUID().toString();
        String urlPublica = propiedades.getBaseUrl() + "/api/deca/" + token;

        byte[] imagenQr = qrGenerator.generar(urlPublica);
        byte[] pdf = pdfGenerator.generar(transporte, imagenQr, urlPublica);

        String rutaPdf = guardarEnDisco(token, pdf);

        transporte.setEstado(EstadoTransporte.GENERADO);

        DocumentoDeca documento = new DocumentoDeca(transporte, token, rutaPdf);
        return DocumentoDecaResponse.from(documentoRepository.save(documento));
    }

    public byte[] obtenerPdfPorToken(String token) {
        DocumentoDeca documento = documentoRepository.findByUrlPublica(token)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún documento con esa URL"));
        try {
            return Files.readAllBytes(Path.of(documento.getRutaPdf()));
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el PDF del documento", e);
        }
    }

    public List<DocumentoDecaResponse> listar() {
        return documentoRepository.findAll().stream()
                .map(DocumentoDecaResponse::from)
                .toList();
    }

    public DocumentoDecaResponse obtener(Long id) {
        return documentoRepository.findById(id)
                .map(DocumentoDecaResponse::from)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el documento con id " + id));
    }

    private String guardarEnDisco(String token, byte[] pdf) {
        try {
            Path directorio = Path.of(propiedades.getDirectorioDocumentos());
            Files.createDirectories(directorio);
            Path rutaArchivo = directorio.resolve("deca_" + token + ".pdf");
            Files.write(rutaArchivo, pdf);
            return rutaArchivo.toString();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el PDF en disco", e);
        }
    }
}


package com.decamanager.deca;

import java.time.LocalDateTime;

public record DocumentoDecaResponse (
    
    Long id,
    Long transporteId,
    String urlPublica,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaModificacion,
    Integer version

) {

    public static DocumentoDecaResponse from(DocumentoDeca documentoDeca) {
        return new DocumentoDecaResponse(
            documentoDeca.getId(),
            documentoDeca.getTransporte().getId(),
            documentoDeca.getUrlPublica(),
            documentoDeca.getFechaCreacion(),
            documentoDeca.getFechaModificacion(),
            documentoDeca.getVersion()
        );
    }
}

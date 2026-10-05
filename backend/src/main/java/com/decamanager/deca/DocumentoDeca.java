package com.decamanager.deca;

import java.time.LocalDateTime;

import com.decamanager.transporte.Transporte;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "documento_deca")
public class DocumentoDeca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transporte_id", nullable = false, unique = true)
    private Transporte transporte;

    @Column(name = "url_publica", nullable = false, unique = true, length = 64)
    private String urlPublica;

    @Column(name = "ruta_pdf", nullable = false, length = 500)
    private String rutaPdf;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(nullable = false)
    private Integer version = 1;

    protected DocumentoDeca() {
        // requerido por JPA
    }

    public DocumentoDeca(Transporte transporte, String urlPublica, String rutaPdf) {
        this.transporte = transporte;
        this.urlPublica = urlPublica;
        this.rutaPdf = rutaPdf;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Transporte getTransporte() { return transporte; }

    public String getUrlPublica() { return urlPublica; }

    public String getRutaPdf() { return rutaPdf; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }

    public Integer getVersion() { return version; }
}


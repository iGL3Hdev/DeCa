package com.decamanager.transporte;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.decamanager.empresa.Empresa;
import com.decamanager.vehiculo.Vehiculo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "transporte")
public class Transporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cargador_id", nullable = false)
    private Empresa cargador;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transportista_id", nullable = false)
    private Empresa transportista;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @Column(nullable = false)
    private LocalDate fechaOperacion;

    @Column(nullable = false, length = 255)
    private String lugarCarga;

    @Column(nullable = false)
    private LocalDate fechaCarga;

    @Column(nullable = false, length = 255)
    private String lugarDescarga;

    @Column(nullable = false)
    private LocalDate fechaDescarga;

    @Column(nullable = false, length = 150)
    private String mercanciaNaturaleza;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal mercanciaPeso;

    @Column(nullable = false, length = 10)
    private String mercanciaUnidad = "TM";

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoTransporte estado = EstadoTransporte.BORRADOR;

    protected Transporte() {
        // requerido por JPA
    }

     public Transporte(Empresa cargador, Empresa transportista, Vehiculo vehiculo,
            LocalDate fechaOperacion, String lugarCarga, LocalDate fechaCarga,
            String lugarDescarga, LocalDate fechaDescarga,
            String mercanciaNaturaleza, BigDecimal mercanciaPeso, String mercanciaUnidad,
            String notas) {
        this.cargador = cargador;
        this.transportista = transportista;
        this.vehiculo = vehiculo;
        this.fechaOperacion = fechaOperacion;
        this.lugarCarga = lugarCarga;
        this.fechaCarga = fechaCarga;
        this.lugarDescarga = lugarDescarga;
        this.fechaDescarga = fechaDescarga;
        this.mercanciaNaturaleza = mercanciaNaturaleza;
        this.mercanciaPeso = mercanciaPeso;
        this.mercanciaUnidad = mercanciaUnidad;
        this.notas = notas;
    }

    public Long getId() { return id; }

    public Empresa getCargador() { return cargador; }
    public void setCargador(Empresa cargador) { this.cargador = cargador; }

    public Empresa getTransportista() { return transportista; }
    public void setTransportista(Empresa transportista) { this.transportista = transportista; }

    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

    public LocalDate getFechaOperacion() { return fechaOperacion; }
    public void setFechaOperacion(LocalDate fechaOperacion) { this.fechaOperacion = fechaOperacion; }

    public String getLugarCarga() { return lugarCarga; }
    public void setLugarCarga(String lugarCarga) { this.lugarCarga = lugarCarga; }

    public LocalDate getFechaCarga() { return fechaCarga; }
    public void setFechaCarga(LocalDate fechaCarga) { this.fechaCarga = fechaCarga; }

    public String getLugarDescarga() { return lugarDescarga; }
    public void setLugarDescarga(String lugarDescarga) { this.lugarDescarga = lugarDescarga; }

    public LocalDate getFechaDescarga() { return fechaDescarga; }
    public void setFechaDescarga(LocalDate fechaDescarga) { this.fechaDescarga = fechaDescarga; }

    public String getMercanciaNaturaleza() { return mercanciaNaturaleza; }
    public void setMercanciaNaturaleza(String mercanciaNaturaleza) { this.mercanciaNaturaleza = mercanciaNaturaleza; }

    public BigDecimal getMercanciaPeso() { return mercanciaPeso; }
    public void setMercanciaPeso(BigDecimal mercanciaPeso) { this.mercanciaPeso = mercanciaPeso; }

    public String getMercanciaUnidad() { return mercanciaUnidad; }
    public void setMercanciaUnidad(String mercanciaUnidad) { this.mercanciaUnidad = mercanciaUnidad; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public EstadoTransporte getEstado() { return estado; }
    public void setEstado(EstadoTransporte estado) { this.estado = estado; }
}

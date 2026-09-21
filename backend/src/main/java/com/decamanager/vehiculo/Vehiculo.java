package com.decamanager.vehiculo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 15)
    private String matricula;

    @Column(nullable = false, length=50)
    private String tipo;

    @Column(name = "matricula_remolque", length=15)
    private String matriculaRemolque;

    @Column(nullable = false)
    private boolean activo = true;

    protected Vehiculo() {
        //requerido por JPA
    }

    public Vehiculo(String matricula, String tipo, String matriculaRemolque) {
        this.matricula = matricula;
        this.tipo = tipo;
        this.matriculaRemolque = matriculaRemolque;
    }

    public Long getId() { return id; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMatriculaRemolque() { return matriculaRemolque; }
    public void setMatriculaRemolque(String matriculaRemolque) {this.matriculaRemolque = matriculaRemolque; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
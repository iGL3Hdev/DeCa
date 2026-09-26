package com.decamanager.empresa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity 
@Table(name = "empresa")
public class Empresa {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, unique = true, length = 20)
    private String nif;

    @Column(length = 255)
    private String domicilio;

    @Column(length = 30)
    private String telefono;

    protected Empresa() {
        //requerido por JPA
    }

    public Empresa(String nombre, String nif, String domicilio, String telefono) {
        this.nombre = nombre;
        this.nif = nif;
        this.domicilio = domicilio;
        this.telefono = telefono;
    }

    public Long getId() {return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    
}

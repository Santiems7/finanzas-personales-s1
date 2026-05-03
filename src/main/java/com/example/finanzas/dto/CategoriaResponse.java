package com.example.finanzas.dto;

import com.example.finanzas.entity.CategoriaOrigen;
import com.example.finanzas.entity.CategoriaTipo;

public class CategoriaResponse {
    private Long id;
    private Long usuarioId;
    private String nombre;
    private CategoriaTipo tipo;
    private CategoriaOrigen origen;
    private String icono;
    private Boolean activa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public CategoriaTipo getTipo() {
        return tipo;
    }

    public void setTipo(CategoriaTipo tipo) {
        this.tipo = tipo;
    }

    public CategoriaOrigen getOrigen() {
        return origen;
    }

    public void setOrigen(CategoriaOrigen origen) {
        this.origen = origen;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
}

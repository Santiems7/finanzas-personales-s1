package com.example.finanzas.dto;

import java.math.BigDecimal;

public class PresupuestoCategoriaResponse {
    private Long id;
    private Long categoriaId;
    private BigDecimal montoLimite;
    private BigDecimal montoEjecutado;
    private BigDecimal porcentajeEjecutado;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public BigDecimal getMontoLimite() { return montoLimite; }
    public void setMontoLimite(BigDecimal montoLimite) { this.montoLimite = montoLimite; }
    public BigDecimal getMontoEjecutado() { return montoEjecutado; }
    public void setMontoEjecutado(BigDecimal montoEjecutado) { this.montoEjecutado = montoEjecutado; }
    public BigDecimal getPorcentajeEjecutado() { return porcentajeEjecutado; }
    public void setPorcentajeEjecutado(BigDecimal porcentajeEjecutado) { this.porcentajeEjecutado = porcentajeEjecutado; }
}

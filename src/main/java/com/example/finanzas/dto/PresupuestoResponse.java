package com.example.finanzas.dto;

import com.example.finanzas.entity.PresupuestoEstado;
import com.example.finanzas.entity.PresupuestoPeriodo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PresupuestoResponse {
    private Long id;
    private Long usuarioId;
    private BigDecimal montoGlobalLimite;
    private BigDecimal montoGlobalEjecutado;
    private BigDecimal porcentajeGlobalEjecutado;
    private PresupuestoPeriodo periodo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private PresupuestoEstado estado;
    private List<PresupuestoCategoriaResponse> categorias;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public BigDecimal getMontoGlobalLimite() { return montoGlobalLimite; }
    public void setMontoGlobalLimite(BigDecimal montoGlobalLimite) { this.montoGlobalLimite = montoGlobalLimite; }
    public BigDecimal getMontoGlobalEjecutado() { return montoGlobalEjecutado; }
    public void setMontoGlobalEjecutado(BigDecimal montoGlobalEjecutado) { this.montoGlobalEjecutado = montoGlobalEjecutado; }
    public BigDecimal getPorcentajeGlobalEjecutado() { return porcentajeGlobalEjecutado; }
    public void setPorcentajeGlobalEjecutado(BigDecimal porcentajeGlobalEjecutado) { this.porcentajeGlobalEjecutado = porcentajeGlobalEjecutado; }
    public PresupuestoPeriodo getPeriodo() { return periodo; }
    public void setPeriodo(PresupuestoPeriodo periodo) { this.periodo = periodo; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public PresupuestoEstado getEstado() { return estado; }
    public void setEstado(PresupuestoEstado estado) { this.estado = estado; }
    public List<PresupuestoCategoriaResponse> getCategorias() { return categorias; }
    public void setCategorias(List<PresupuestoCategoriaResponse> categorias) { this.categorias = categorias; }
}

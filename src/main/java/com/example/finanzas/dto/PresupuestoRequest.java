package com.example.finanzas.dto;

import com.example.finanzas.entity.PresupuestoPeriodo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PresupuestoRequest {
    @Positive(message = "El límite global debe ser mayor a cero")
    private BigDecimal montoGlobalLimite;

    @NotNull(message = "El periodo es obligatorio")
    private PresupuestoPeriodo periodo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @Valid
    private List<PresupuestoCategoriaRequest> categorias = new ArrayList<>();

    public BigDecimal getMontoGlobalLimite() { return montoGlobalLimite; }
    public void setMontoGlobalLimite(BigDecimal montoGlobalLimite) { this.montoGlobalLimite = montoGlobalLimite; }
    public PresupuestoPeriodo getPeriodo() { return periodo; }
    public void setPeriodo(PresupuestoPeriodo periodo) { this.periodo = periodo; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public List<PresupuestoCategoriaRequest> getCategorias() { return categorias; }
    public void setCategorias(List<PresupuestoCategoriaRequest> categorias) { this.categorias = categorias; }
}

package com.example.finanzas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class PresupuestoCategoriaRequest {
    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "El límite por categoría es obligatorio")
    @Positive(message = "El límite por categoría debe ser mayor a cero")
    private BigDecimal montoLimite;

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public BigDecimal getMontoLimite() { return montoLimite; }
    public void setMontoLimite(BigDecimal montoLimite) { this.montoLimite = montoLimite; }
}

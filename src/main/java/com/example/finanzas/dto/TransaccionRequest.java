package com.example.finanzas.dto;

import com.example.finanzas.entity.TransaccionTipo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TransaccionRequest {
    @NotNull(message = "El tipo es obligatorio")
    private TransaccionTipo tipo;

    @NotNull(message = "Debes seleccionar una cuenta")
    private Long cuentaId;

    @NotNull(message = "Debes seleccionar una categoría")
    private Long categoriaId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    @NotNull(message = "La fecha es obligatoria")
    private OffsetDateTime fecha;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;

    public TransaccionTipo getTipo() { return tipo; }
    public void setTipo(TransaccionTipo tipo) { this.tipo = tipo; }
    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public OffsetDateTime getFecha() { return fecha; }
    public void setFecha(OffsetDateTime fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}

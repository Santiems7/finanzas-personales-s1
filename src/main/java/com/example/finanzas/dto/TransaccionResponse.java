package com.example.finanzas.dto;

import com.example.finanzas.entity.TransaccionTipo;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TransaccionResponse {
    private Long id;
    private Long usuarioId;
    private Long cuentaId;
    private Long categoriaId;
    private TransaccionTipo tipo;
    private BigDecimal monto;
    private OffsetDateTime fecha;
    private String descripcion;
    private BigDecimal saldoCuenta;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public TransaccionTipo getTipo() { return tipo; }
    public void setTipo(TransaccionTipo tipo) { this.tipo = tipo; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public OffsetDateTime getFecha() { return fecha; }
    public void setFecha(OffsetDateTime fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getSaldoCuenta() { return saldoCuenta; }
    public void setSaldoCuenta(BigDecimal saldoCuenta) { this.saldoCuenta = saldoCuenta; }
}

package com.example.finanzas.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "presupuesto", schema = "finanzas")
public class Presupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "monto_global_limite", precision = 14, scale = 2)
    private BigDecimal montoGlobalLimite;

    @Column(name = "monto_global_ejecutado", precision = 14, scale = 2)
    private BigDecimal montoGlobalEjecutado = BigDecimal.ZERO;

    @Column(name = "alerta_global_generada")
    private boolean alertaGlobalGenerada = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PresupuestoPeriodo periodo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PresupuestoEstado estado = PresupuestoEstado.BORRADOR;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PresupuestoCategoria> categorias = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public BigDecimal getMontoGlobalLimite() { return montoGlobalLimite; }
    public void setMontoGlobalLimite(BigDecimal montoGlobalLimite) { this.montoGlobalLimite = montoGlobalLimite; }
    public PresupuestoPeriodo getPeriodo() { return periodo; }
    public void setPeriodo(PresupuestoPeriodo periodo) { this.periodo = periodo; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public PresupuestoEstado getEstado() { return estado; }
    public void setEstado(PresupuestoEstado estado) { this.estado = estado; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public List<PresupuestoCategoria> getCategorias() { return categorias; }
    public void setCategorias(List<PresupuestoCategoria> categorias) { this.categorias = categorias; }
    public BigDecimal getMontoGlobalEjecutado() { return montoGlobalEjecutado; }
    public void setMontoGlobalEjecutado(BigDecimal montoGlobalEjecutado) { this.montoGlobalEjecutado = montoGlobalEjecutado; }
    public boolean isAlertaGlobalGenerada() { return alertaGlobalGenerada; }
    public void setAlertaGlobalGenerada(boolean alertaGlobalGenerada) { this.alertaGlobalGenerada = alertaGlobalGenerada; }
}

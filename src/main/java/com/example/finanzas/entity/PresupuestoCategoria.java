package com.example.finanzas.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "presupuesto_categoria", schema = "finanzas")
public class PresupuestoCategoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presupuesto_id", nullable = false)
    private Presupuesto presupuesto;

    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    @Column(name = "monto_limite", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoLimite;

    @Column(name = "monto_ejecutado", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoEjecutado = BigDecimal.ZERO;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Presupuesto getPresupuesto() { return presupuesto; }
    public void setPresupuesto(Presupuesto presupuesto) { this.presupuesto = presupuesto; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public BigDecimal getMontoLimite() { return montoLimite; }
    public void setMontoLimite(BigDecimal montoLimite) { this.montoLimite = montoLimite; }
    public BigDecimal getMontoEjecutado() { return montoEjecutado; }
    public void setMontoEjecutado(BigDecimal montoEjecutado) { this.montoEjecutado = montoEjecutado; }
}

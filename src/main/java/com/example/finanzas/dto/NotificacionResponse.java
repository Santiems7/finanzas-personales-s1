package com.example.finanzas.dto;

import com.example.finanzas.entity.NotificacionTipo;
import java.time.OffsetDateTime;

public class NotificacionResponse {
    private Long id;
    private String mensaje;
    private NotificacionTipo tipo;
    private Long referenciaId;
    private boolean leida;
    private OffsetDateTime fechaCreacion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public NotificacionTipo getTipo() { return tipo; }
    public void setTipo(NotificacionTipo tipo) { this.tipo = tipo; }
    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}

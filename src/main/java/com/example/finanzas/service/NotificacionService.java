package com.example.finanzas.service;

import com.example.finanzas.dto.NotificacionResponse;
import com.example.finanzas.entity.*;
import com.example.finanzas.exception.UnauthorizedException;
import com.example.finanzas.repository.NotificacionRepository;
import com.example.finanzas.repository.PresupuestoCategoriaRepository;
import com.example.finanzas.repository.PresupuestoRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {
    private static final BigDecimal UMBRAL = BigDecimal.valueOf(0.8);

    private final NotificacionRepository notificacionRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final PresupuestoCategoriaRepository presupuestoCategoriaRepository;

    public NotificacionService(NotificacionRepository notificacionRepository,
                               PresupuestoRepository presupuestoRepository,
                               PresupuestoCategoriaRepository presupuestoCategoriaRepository) {
        this.notificacionRepository = notificacionRepository;
        this.presupuestoRepository = presupuestoRepository;
        this.presupuestoCategoriaRepository = presupuestoCategoriaRepository;
    }

    /**
     * Evalúa si se alcanzó el 80% del límite global o de la categoría y genera la alerta si aún no fue emitida.
     * Llamado desde TransaccionService dentro de la misma transacción atómica.
     */
    public void evaluarAlertas(Long usuarioId, Presupuesto presupuesto,
                               PresupuestoCategoria pc, String nombreCategoria) {
        evaluarAlertaGlobal(usuarioId, presupuesto);
        if (pc != null) {
            evaluarAlertaCategoria(usuarioId, presupuesto, pc, nombreCategoria);
        }
    }

    public List<NotificacionResponse> listar(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public NotificacionResponse marcarLeida(Long usuarioId, Long notificacionId) {
        Notificacion n = notificacionRepository.findByIdAndUsuarioId(notificacionId, usuarioId)
                .orElseThrow(() -> new UnauthorizedException("La notificación no existe o no pertenece al usuario"));
        n.setLeida(true);
        return toResponse(notificacionRepository.save(n));
    }

    private void evaluarAlertaGlobal(Long usuarioId, Presupuesto presupuesto) {
        if (presupuesto.getMontoGlobalLimite() == null) return;
        if (presupuesto.isAlertaGlobalGenerada()) return;
        BigDecimal umbral = presupuesto.getMontoGlobalLimite().multiply(UMBRAL);
        if (presupuesto.getMontoGlobalEjecutado().compareTo(umbral) >= 0) {
            Notificacion n = new Notificacion();
            n.setUsuarioId(usuarioId);
            n.setMensaje("Has alcanzado el 80% de tu presupuesto global");
            n.setTipo(NotificacionTipo.PRESUPUESTO_GLOBAL);
            n.setReferenciaId(presupuesto.getId());
            notificacionRepository.save(n);
            presupuesto.setAlertaGlobalGenerada(true);
            presupuestoRepository.save(presupuesto);
        }
    }

    private void evaluarAlertaCategoria(Long usuarioId, Presupuesto presupuesto,
                                        PresupuestoCategoria pc, String nombreCategoria) {
        if (pc.isAlertaGenerada()) return;
        BigDecimal umbral = pc.getMontoLimite().multiply(UMBRAL);
        if (pc.getMontoEjecutado().compareTo(umbral) >= 0) {
            Notificacion n = new Notificacion();
            n.setUsuarioId(usuarioId);
            n.setMensaje("Has alcanzado el 80% del límite en la categoría " + nombreCategoria);
            n.setTipo(NotificacionTipo.PRESUPUESTO_CATEGORIA);
            n.setReferenciaId(pc.getId());
            notificacionRepository.save(n);
            pc.setAlertaGenerada(true);
            presupuestoCategoriaRepository.save(pc);
        }
    }

    private NotificacionResponse toResponse(Notificacion n) {
        NotificacionResponse r = new NotificacionResponse();
        r.setId(n.getId());
        r.setMensaje(n.getMensaje());
        r.setTipo(n.getTipo());
        r.setReferenciaId(n.getReferenciaId());
        r.setLeida(n.isLeida());
        r.setFechaCreacion(n.getFechaCreacion());
        return r;
    }
}

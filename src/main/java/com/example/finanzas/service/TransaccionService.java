package com.example.finanzas.service;

import com.example.finanzas.dto.TransaccionRequest;
import com.example.finanzas.dto.TransaccionResponse;
import com.example.finanzas.entity.*;
import com.example.finanzas.exception.BusinessException;
import com.example.finanzas.exception.UnauthorizedException;
import com.example.finanzas.repository.CategoriaRepository;
import com.example.finanzas.repository.CuentaRepository;
import com.example.finanzas.repository.PresupuestoCategoriaRepository;
import com.example.finanzas.repository.PresupuestoRepository;
import com.example.finanzas.repository.TransaccionRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TransaccionService {
    private final TransaccionRepository transaccionRepository;
    private final CuentaRepository cuentaRepository;
    private final CategoriaRepository categoriaRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final PresupuestoCategoriaRepository presupuestoCategoriaRepository;
    private final NotificacionService notificacionService;

    public TransaccionService(TransaccionRepository transaccionRepository,
                              CuentaRepository cuentaRepository,
                              CategoriaRepository categoriaRepository,
                              PresupuestoRepository presupuestoRepository,
                              PresupuestoCategoriaRepository presupuestoCategoriaRepository,
                              NotificacionService notificacionService) {
        this.transaccionRepository = transaccionRepository;
        this.cuentaRepository = cuentaRepository;
        this.categoriaRepository = categoriaRepository;
        this.presupuestoRepository = presupuestoRepository;
        this.presupuestoCategoriaRepository = presupuestoCategoriaRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public TransaccionResponse create(Long usuarioId, TransaccionRequest request) {
        validateBase(request);
        if (request.getTipo() == TransaccionTipo.TRANSFERENCIA) {
            throw new BusinessException("Las transferencias no hacen parte de este MVP");
        }

        Cuenta cuenta = cuentaRepository.findOwnedByUsuarioId(usuarioId, request.getCuentaId())
                .orElseThrow(() -> new UnauthorizedException("La cuenta no existe o no pertenece al usuario"));
        if (!Boolean.TRUE.equals(cuenta.getActivo())) {
            throw new BusinessException("La cuenta está inactiva");
        }

        Categoria categoria = categoriaRepository.findVisibleByUser(usuarioId, request.getCategoriaId())
                .orElseThrow(() -> new UnauthorizedException("La categoría no existe o no pertenece al usuario"));
        if (!Boolean.TRUE.equals(categoria.getActiva())) {
            throw new BusinessException("La categoría está inactiva");
        }
        validateCategoryType(request.getTipo(), categoria);

        BigDecimal monto = request.getMonto();
        if (request.getTipo() == TransaccionTipo.INGRESO) {
            cuenta.setSaldo(cuenta.getSaldo().add(monto));
        } else if (request.getTipo() == TransaccionTipo.EGRESO) {
            cuenta.setSaldo(cuenta.getSaldo().subtract(monto));
            imputarPresupuestoActivo(usuarioId, categoria, monto);
        }

        Transaccion transaccion = new Transaccion();
        transaccion.setUsuarioId(usuarioId);
        transaccion.setCuentaId(cuenta.getId());
        transaccion.setCategoriaId(categoria.getId());
        transaccion.setTipo(request.getTipo());
        transaccion.setMonto(monto);
        transaccion.setFecha(request.getFecha());
        transaccion.setDescripcion(blankToNull(request.getDescripcion()));

        cuentaRepository.save(cuenta);
        transaccion = transaccionRepository.save(transaccion);
        return toResponse(transaccion, cuenta.getSaldo());
    }

    public List<TransaccionResponse> history(Long usuarioId,
                                             Long cuentaId,
                                             Long categoriaId,
                                             TransaccionTipo tipo,
                                             OffsetDateTime fechaDesde,
                                             OffsetDateTime fechaHasta,
                                             String descripcion) {
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        if (cuentaId != null) {
            cuentaRepository.findOwnedByUsuarioId(usuarioId, cuentaId)
                    .orElseThrow(() -> new UnauthorizedException("La cuenta no existe o no pertenece al usuario"));
        }
        if (categoriaId != null) {
            categoriaRepository.findVisibleByUser(usuarioId, categoriaId)
                    .orElseThrow(() -> new UnauthorizedException("La categoría no existe o no pertenece al usuario"));
        }
        String descripcionPattern = (descripcion != null && !descripcion.isBlank())
                ? "%" + descripcion.toLowerCase().trim() + "%" : null;
        return transaccionRepository.findHistorial(usuarioId, cuentaId, categoriaId, tipo,
                        fechaDesde, fechaHasta, descripcionPattern)
                .stream().map(t -> toResponse(t, null)).toList();
    }

    private void validateBase(TransaccionRequest request) {
        if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto debe ser mayor a cero");
        }
        if (request.getFecha() != null && request.getFecha().isAfter(OffsetDateTime.now())) {
            throw new BusinessException("La fecha no puede ser posterior a la fecha actual");
        }
    }

    private void validateCategoryType(TransaccionTipo tipo, Categoria categoria) {
        if (tipo == TransaccionTipo.INGRESO && categoria.getTipo() == CategoriaTipo.EGRESO) {
            throw new BusinessException("La categoría seleccionada no permite ingresos");
        }
        if (tipo == TransaccionTipo.EGRESO && categoria.getTipo() == CategoriaTipo.INGRESO) {
            throw new BusinessException("La categoría seleccionada no permite gastos");
        }
    }

    private void imputarPresupuestoActivo(Long usuarioId, Categoria categoria, BigDecimal monto) {
        presupuestoRepository.findByUsuarioIdAndEstado(usuarioId, PresupuestoEstado.ACTIVO)
                .ifPresent(presupuesto -> {
                    presupuesto.setMontoGlobalEjecutado(presupuesto.getMontoGlobalEjecutado().add(monto));
                    presupuestoRepository.save(presupuesto);

                    Optional<PresupuestoCategoria> optPc = presupuestoCategoriaRepository
                            .findByPresupuestoIdAndCategoriaId(presupuesto.getId(), categoria.getId());
                    optPc.ifPresent(pc -> {
                        pc.setMontoEjecutado(pc.getMontoEjecutado().add(monto));
                        presupuestoCategoriaRepository.save(pc);
                    });

                    notificacionService.evaluarAlertas(usuarioId, presupuesto,
                            optPc.orElse(null), categoria.getNombre());
                });
    }

    private String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return value.trim();
    }

    private TransaccionResponse toResponse(Transaccion transaccion, BigDecimal saldoCuenta) {
        TransaccionResponse response = new TransaccionResponse();
        response.setId(transaccion.getId());
        response.setUsuarioId(transaccion.getUsuarioId());
        response.setCuentaId(transaccion.getCuentaId());
        response.setCategoriaId(transaccion.getCategoriaId());
        response.setTipo(transaccion.getTipo());
        response.setMonto(transaccion.getMonto());
        response.setFecha(transaccion.getFecha());
        response.setDescripcion(transaccion.getDescripcion());
        response.setSaldoCuenta(saldoCuenta);
        return response;
    }
}

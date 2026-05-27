package com.example.finanzas.service;

import com.example.finanzas.dto.*;
import com.example.finanzas.entity.*;
import com.example.finanzas.exception.BusinessException;
import com.example.finanzas.exception.UnauthorizedException;
import com.example.finanzas.repository.CategoriaRepository;
import com.example.finanzas.repository.PresupuestoRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class PresupuestoService {
    private final PresupuestoRepository presupuestoRepository;
    private final CategoriaRepository categoriaRepository;

    public PresupuestoService(PresupuestoRepository presupuestoRepository, CategoriaRepository categoriaRepository) {
        this.presupuestoRepository = presupuestoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public PresupuestoResponse createDraft(Long usuarioId, PresupuestoRequest request) {
        if (presupuestoRepository.existsByUsuarioIdAndEstado(usuarioId, PresupuestoEstado.ACTIVO)) {
            throw new BusinessException("Ya existe un presupuesto activo. Debes cerrarlo o esperar a que venza");
        }

        validateCategoryLimits(usuarioId, request);

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setUsuarioId(usuarioId);
        presupuesto.setPeriodo(request.getPeriodo());
        presupuesto.setFechaInicio(request.getFechaInicio());
        presupuesto.setFechaFin(calculateEndDate(request.getFechaInicio(), request.getPeriodo()));
        presupuesto.setMontoGlobalLimite(request.getMontoGlobalLimite());
        presupuesto.setEstado(PresupuestoEstado.BORRADOR);

        if (request.getCategorias() != null) {
            for (PresupuestoCategoriaRequest item : request.getCategorias()) {
                PresupuestoCategoria pc = new PresupuestoCategoria();
                pc.setPresupuesto(presupuesto);
                pc.setCategoriaId(item.getCategoriaId());
                pc.setMontoLimite(item.getMontoLimite());
                pc.setMontoEjecutado(BigDecimal.ZERO);
                presupuesto.getCategorias().add(pc);
            }
        }

        return toResponse(presupuestoRepository.save(presupuesto));
    }

    @Transactional
    public PresupuestoResponse activate(Long usuarioId, Long presupuestoId) {
        if (presupuestoRepository.existsByUsuarioIdAndEstado(usuarioId, PresupuestoEstado.ACTIVO)) {
            throw new BusinessException("Ya existe un presupuesto activo. Debes cerrarlo o esperar a que venza");
        }
        Presupuesto presupuesto = presupuestoRepository.findByIdAndUsuarioId(presupuestoId, usuarioId)
                .orElseThrow(() -> new UnauthorizedException("El presupuesto no existe o no pertenece al usuario"));
        if (presupuesto.getEstado() != PresupuestoEstado.BORRADOR) {
            throw new BusinessException("Solo se pueden activar presupuestos en estado BORRADOR");
        }
        presupuesto.setEstado(PresupuestoEstado.ACTIVO);
        return toResponse(presupuestoRepository.save(presupuesto));
    }

    public List<PresupuestoResponse> list(Long usuarioId) {
        return presupuestoRepository.findByUsuarioIdOrderByFechaInicioDescIdDesc(usuarioId)
                .stream().map(this::toResponse).toList();
    }

    public PresupuestoResponse get(Long usuarioId, Long presupuestoId) {
        Presupuesto presupuesto = presupuestoRepository.findByIdAndUsuarioId(presupuestoId, usuarioId)
                .orElseThrow(() -> new UnauthorizedException("El presupuesto no existe o no pertenece al usuario"));
        return toResponse(presupuesto);
    }

    @Transactional
    public Optional<PresupuestoResponse> getActivo(Long usuarioId) {
        Optional<Presupuesto> opt = presupuestoRepository.findByUsuarioIdAndEstado(usuarioId, PresupuestoEstado.ACTIVO);
        if (opt.isEmpty()) return Optional.empty();

        Presupuesto presupuesto = opt.get();
        if (presupuesto.getFechaFin().isBefore(LocalDate.now())) {
            presupuesto.setEstado(PresupuestoEstado.VENCIDO);
            presupuestoRepository.save(presupuesto);
            crearBorradorDesde(presupuesto);
            return Optional.empty();
        }
        return Optional.of(toResponse(presupuesto));
    }

    private Presupuesto crearBorradorDesde(Presupuesto plantilla) {
        Presupuesto borrador = new Presupuesto();
        borrador.setUsuarioId(plantilla.getUsuarioId());
        borrador.setPeriodo(plantilla.getPeriodo());
        borrador.setFechaInicio(plantilla.getFechaFin());
        borrador.setFechaFin(calculateEndDate(plantilla.getFechaFin(), plantilla.getPeriodo()));
        borrador.setMontoGlobalLimite(plantilla.getMontoGlobalLimite());
        borrador.setEstado(PresupuestoEstado.BORRADOR);
        borrador.setMontoGlobalEjecutado(BigDecimal.ZERO);
        borrador.setAlertaGlobalGenerada(false);
        for (PresupuestoCategoria pc : plantilla.getCategorias()) {
            PresupuestoCategoria nueva = new PresupuestoCategoria();
            nueva.setPresupuesto(borrador);
            nueva.setCategoriaId(pc.getCategoriaId());
            nueva.setMontoLimite(pc.getMontoLimite());
            nueva.setMontoEjecutado(BigDecimal.ZERO);
            nueva.setAlertaGenerada(false);
            borrador.getCategorias().add(nueva);
        }
        return presupuestoRepository.save(borrador);
    }

    private void validateCategoryLimits(Long usuarioId, PresupuestoRequest request) {
        BigDecimal total = BigDecimal.ZERO;
        Set<Long> seen = new HashSet<>();
        if (request.getCategorias() != null) {
            for (PresupuestoCategoriaRequest item : request.getCategorias()) {
                if (!seen.add(item.getCategoriaId())) {
                    throw new BusinessException("No puedes repetir categorías en el mismo presupuesto");
                }
                Categoria categoria = categoriaRepository.findVisibleByUser(usuarioId, item.getCategoriaId())
                        .orElseThrow(
                                () -> new UnauthorizedException("La categoría no existe o no pertenece al usuario"));
                if (!Boolean.TRUE.equals(categoria.getActiva())) {
                    throw new BusinessException("La categoría está inactiva");
                }
                total = total.add(item.getMontoLimite());
            }
        }
        if (request.getMontoGlobalLimite() != null && total.compareTo(request.getMontoGlobalLimite()) > 0) {
            throw new BusinessException("La suma de límites por categoría no puede superar el monto global");
        }
    }

    private LocalDate calculateEndDate(LocalDate start, PresupuestoPeriodo periodo) {
        return switch (periodo) {
            case MENSUAL -> start.plusMonths(1);
            case TRIMESTRAL -> start.plusMonths(3);
            case SEMESTRAL -> start.plusMonths(6);
            case ANUAL -> start.plusYears(1);
        };
    }

    private PresupuestoResponse toResponse(Presupuesto presupuesto) {
        PresupuestoResponse response = new PresupuestoResponse();
        response.setId(presupuesto.getId());
        response.setUsuarioId(presupuesto.getUsuarioId());
        response.setMontoGlobalLimite(presupuesto.getMontoGlobalLimite());
        response.setMontoGlobalEjecutado(presupuesto.getMontoGlobalEjecutado());
        response.setPorcentajeGlobalEjecutado(calcularPorcentaje(
                presupuesto.getMontoGlobalEjecutado(), presupuesto.getMontoGlobalLimite()));
        response.setPeriodo(presupuesto.getPeriodo());
        response.setFechaInicio(presupuesto.getFechaInicio());
        response.setFechaFin(presupuesto.getFechaFin());
        response.setEstado(presupuesto.getEstado());
        response.setCategorias(presupuesto.getCategorias().stream().map(this::toCategoriaResponse).toList());
        return response;
    }

    private PresupuestoCategoriaResponse toCategoriaResponse(PresupuestoCategoria pc) {
        PresupuestoCategoriaResponse response = new PresupuestoCategoriaResponse();
        response.setId(pc.getId());
        response.setCategoriaId(pc.getCategoriaId());
        response.setMontoLimite(pc.getMontoLimite());
        response.setMontoEjecutado(pc.getMontoEjecutado());
        response.setPorcentajeEjecutado(calcularPorcentaje(pc.getMontoEjecutado(), pc.getMontoLimite()));
        return response;
    }

    private BigDecimal calcularPorcentaje(BigDecimal ejecutado, BigDecimal limite) {
        if (limite == null || limite.compareTo(BigDecimal.ZERO) == 0) return null;
        return ejecutado.multiply(BigDecimal.valueOf(100))
                .divide(limite, 2, RoundingMode.HALF_UP);
    }
}

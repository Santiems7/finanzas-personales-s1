package com.example.finanzas.repository;

import com.example.finanzas.entity.Presupuesto;
import com.example.finanzas.entity.PresupuestoEstado;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {
    boolean existsByUsuarioIdAndEstado(Long usuarioId, PresupuestoEstado estado);

    Optional<Presupuesto> findByUsuarioIdAndEstado(Long usuarioId, PresupuestoEstado estado);

    Optional<Presupuesto> findByIdAndUsuarioId(Long id, Long usuarioId);

    List<Presupuesto> findByUsuarioIdOrderByFechaInicioDescIdDesc(Long usuarioId);
}

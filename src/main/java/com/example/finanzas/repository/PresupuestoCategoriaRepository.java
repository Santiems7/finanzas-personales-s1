package com.example.finanzas.repository;

import com.example.finanzas.entity.PresupuestoCategoria;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresupuestoCategoriaRepository extends JpaRepository<PresupuestoCategoria, Long> {
    Optional<PresupuestoCategoria> findByPresupuestoIdAndCategoriaId(Long presupuestoId, Long categoriaId);
}

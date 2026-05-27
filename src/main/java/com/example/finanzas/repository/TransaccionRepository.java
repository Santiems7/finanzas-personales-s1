package com.example.finanzas.repository;

import com.example.finanzas.entity.Transaccion;
import com.example.finanzas.entity.TransaccionTipo;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    boolean existsByCuentaId(Long cuentaId);

    boolean existsByCategoriaId(Long categoriaId);

    @Query("""
            select t from Transaccion t
            where t.usuarioId = :usuarioId
              and (:cuentaId is null or t.cuentaId = :cuentaId)
              and (:categoriaId is null or t.categoriaId = :categoriaId)
              and (:tipo is null or t.tipo = :tipo)
              and (cast(:fechaDesde as offsetdatetime) is null or t.fecha >= :fechaDesde)
              and (cast(:fechaHasta as offsetdatetime) is null or t.fecha <= :fechaHasta)
              and (cast(:descripcionPattern as string) is null or lower(t.descripcion) like :descripcionPattern)
            order by t.fecha desc, t.id desc
            """)
    List<Transaccion> findHistorial(@Param("usuarioId") Long usuarioId,
            @Param("cuentaId") Long cuentaId,
            @Param("categoriaId") Long categoriaId,
            @Param("tipo") TransaccionTipo tipo,
            @Param("fechaDesde") OffsetDateTime fechaDesde,
            @Param("fechaHasta") OffsetDateTime fechaHasta,
            @Param("descripcionPattern") String descripcionPattern);
}

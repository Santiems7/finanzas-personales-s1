package com.example.finanzas.repository;

import com.example.finanzas.entity.Categoria;
import com.example.finanzas.entity.CategoriaTipo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    @Query("""
            select c from Categoria c
            where c.activa = true
              and (c.usuarioId is null or c.usuarioId = :usuarioId)
            order by c.origen, c.nombre
            """)
    List<Categoria> findActiveForUser(@Param("usuarioId") Long usuarioId);

    @Query("""
            select c from Categoria c
            where c.activa = true
              and (c.usuarioId is null or c.usuarioId = :usuarioId)
              and (:tipo is null or c.tipo = :tipo or c.tipo = com.example.finanzas.entity.CategoriaTipo.AMBOS)
            order by c.origen, c.nombre
            """)
    List<Categoria> findActiveForUserByTipo(@Param("usuarioId") Long usuarioId, @Param("tipo") CategoriaTipo tipo);

    @Query("""
            select c from Categoria c
            where c.id = :categoriaId
              and (c.usuarioId is null or c.usuarioId = :usuarioId)
            """)
    Optional<Categoria> findVisibleByUser(@Param("usuarioId") Long usuarioId, @Param("categoriaId") Long categoriaId);

    @Query("""
            select count(c) > 0 from Categoria c
            where c.activa = true
              and (c.usuarioId is null or c.usuarioId = :usuarioId)
              and lower(c.nombre) = lower(:nombre)
              and (:excludeId is null or c.id <> :excludeId)
            """)
    boolean existsDuplicateForUser(@Param("usuarioId") Long usuarioId,
            @Param("nombre") String nombre,
            @Param("excludeId") Long excludeId);

    @Query("""
            select count(c) from Categoria c
            where c.activa = true
              and (c.usuarioId is null or c.usuarioId = :usuarioId)
            """)
    long countActiveForUser(@Param("usuarioId") Long usuarioId);
}

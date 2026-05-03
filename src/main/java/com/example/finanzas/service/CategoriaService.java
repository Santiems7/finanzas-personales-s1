package com.example.finanzas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.finanzas.dto.CategoriaRequest;
import com.example.finanzas.dto.CategoriaResponse;
import com.example.finanzas.dto.CategoriaUpdateRequest;
import com.example.finanzas.entity.Categoria;
import com.example.finanzas.entity.CategoriaOrigen;
import com.example.finanzas.entity.CategoriaTipo;
import com.example.finanzas.exception.BusinessException;
import com.example.finanzas.exception.UnauthorizedException;
import com.example.finanzas.repository.CategoriaRepository;
import com.example.finanzas.repository.TransaccionRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final TransaccionRepository transaccionRepository;

    public CategoriaService(CategoriaRepository categoriaRepository,
            TransaccionRepository transaccionRepository) {
        this.categoriaRepository = categoriaRepository;
        this.transaccionRepository = transaccionRepository;
    }

    public List<CategoriaResponse> list(Long usuarioId, CategoriaTipo tipo) {
        List<Categoria> categorias = tipo == null
                ? categoriaRepository.findActiveForUser(usuarioId)
                : categoriaRepository.findActiveForUserByTipo(usuarioId, tipo);
        return categorias.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CategoriaResponse create(Long usuarioId, CategoriaRequest request) {
        String nombre = normalizeName(request.getNombre());

        Categoria categoria = new Categoria();
        categoria.setUsuarioId(usuarioId);
        categoria.setNombre(nombre);
        categoria.setTipo(request.getTipo());
        categoria.setOrigen(CategoriaOrigen.USUARIO);
        categoria.setIcono(blankToNull(request.getIcono()));
        categoria.setActiva(true);
        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse update(Long usuarioId, Long categoriaId, CategoriaUpdateRequest request) {
        Categoria categoria = categoriaRepository.findVisibleByUser(usuarioId, categoriaId)
                .orElseThrow(() -> new UnauthorizedException("La categoría no existe o no pertenece al usuario"));

        if (!Boolean.TRUE.equals(categoria.getActiva())) {
            throw new BusinessException("La categoría está inactiva");
        }

        String nombre = normalizeName(request.getNombre());
        validateDuplicate(usuarioId, nombre, categoriaId);

        categoria.setNombre(nombre);
        categoria.setIcono(blankToNull(request.getIcono()));
        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void delete(Long usuarioId, Long categoriaId) {
        Categoria categoria = categoriaRepository.findVisibleByUser(usuarioId, categoriaId)
                .orElseThrow(() -> new UnauthorizedException("La categoría no existe o no pertenece al usuario"));

        long activeCount = categoriaRepository.countActiveForUser(usuarioId);
        if (activeCount <= 1) {
            throw new BusinessException("Debes tener al menos una categoría activa para poder registrar transacciones");
        }

        // Siempre se usa soft delete. Si tiene transacciones, se conserva la referencia
        // histórica.
        transaccionRepository.existsByCategoriaId(categoriaId);
        categoria.setActiva(false);
        categoriaRepository.save(categoria);
    }

    private void validateDuplicate(Long usuarioId, String nombre, Long excludeId) {
        if (categoriaRepository.existsDuplicateForUser(usuarioId, nombre, excludeId)) {
            throw new BusinessException("Ya tienes una categoría con ese nombre");
        }
    }

    private String normalizeName(String value) {
        return value == null ? null : value.trim();
    }

    private String blankToNull(String value) {
        if (value == null || value.trim().isEmpty())
            return null;
        return value.trim();
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        CategoriaResponse response = new CategoriaResponse();
        response.setId(categoria.getId());
        response.setUsuarioId(categoria.getUsuarioId());
        response.setNombre(categoria.getNombre());
        response.setTipo(categoria.getTipo());
        response.setOrigen(categoria.getOrigen());
        response.setIcono(categoria.getIcono());
        response.setActiva(categoria.getActiva());
        return response;
    }
}

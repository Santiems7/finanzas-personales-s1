package com.example.finanzas.controller;

import com.example.finanzas.dto.CategoriaRequest;
import com.example.finanzas.dto.CategoriaResponse;
import com.example.finanzas.dto.CategoriaUpdateRequest;
import com.example.finanzas.dto.MessageResponse;
import com.example.finanzas.entity.CategoriaTipo;
import com.example.finanzas.security.CustomUserDetails;
import com.example.finanzas.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorías")
@SecurityRequirement(name = "bearerAuth")
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    @Operation(summary = "Crear categoría personalizada")
    public ResponseEntity<CategoriaResponse> create(@AuthenticationPrincipal CustomUserDetails user,
                                                    @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.create(user.getId(), request));
    }

    @GetMapping
    @Operation(summary = "Listar categorías activas del usuario")
    public ResponseEntity<List<CategoriaResponse>> list(@AuthenticationPrincipal CustomUserDetails user,
                                                        @RequestParam(required = false) CategoriaTipo tipo) {
        return ResponseEntity.ok(categoriaService.list(user.getId(), tipo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar nombre e icono de categoría")
    public ResponseEntity<CategoriaResponse> update(@AuthenticationPrincipal CustomUserDetails user,
                                                    @PathVariable Long id,
                                                    @Valid @RequestBody CategoriaUpdateRequest request) {
        return ResponseEntity.ok(categoriaService.update(user.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría con soft delete")
    public ResponseEntity<MessageResponse> delete(@AuthenticationPrincipal CustomUserDetails user,
                                                  @PathVariable Long id) {
        categoriaService.delete(user.getId(), id);
        return ResponseEntity.ok(new MessageResponse("Categoría eliminada correctamente"));
    }
}

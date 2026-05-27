package com.example.finanzas.controller;

import com.example.finanzas.dto.PresupuestoRequest;
import com.example.finanzas.dto.PresupuestoResponse;
import com.example.finanzas.security.CustomUserDetails;
import com.example.finanzas.service.PresupuestoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/presupuestos")
@Tag(name = "Presupuestos")
@SecurityRequirement(name = "bearerAuth")
public class PresupuestoController {
    private final PresupuestoService presupuestoService;

    public PresupuestoController(PresupuestoService presupuestoService) {
        this.presupuestoService = presupuestoService;
    }

    @PostMapping
    @Operation(summary = "Crear presupuesto en estado BORRADOR")
    public ResponseEntity<PresupuestoResponse> createDraft(@AuthenticationPrincipal CustomUserDetails user,
                                                           @Valid @RequestBody PresupuestoRequest request) {
        return ResponseEntity.ok(presupuestoService.createDraft(user.getId(), request));
    }

    @PutMapping("/{id}/activar")
    @Operation(summary = "Activar presupuesto")
    public ResponseEntity<PresupuestoResponse> activate(@AuthenticationPrincipal CustomUserDetails user,
                                                        @PathVariable Long id) {
        return ResponseEntity.ok(presupuestoService.activate(user.getId(), id));
    }

    @GetMapping
    @Operation(summary = "Listar presupuestos del usuario")
    public ResponseEntity<List<PresupuestoResponse>> list(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(presupuestoService.list(user.getId()));
    }

    @GetMapping("/activo")
    @Operation(summary = "Consultar presupuesto activo con ejecución en tiempo real")
    public ResponseEntity<?> getActivo(@AuthenticationPrincipal CustomUserDetails user) {
        Optional<PresupuestoResponse> activo = presupuestoService.getActivo(user.getId());
        if (activo.isEmpty()) {
            return ResponseEntity.ok(java.util.Map.of("sinPresupuestoActivo", true));
        }
        return ResponseEntity.ok(activo.get());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar presupuesto por id")
    public ResponseEntity<PresupuestoResponse> get(@AuthenticationPrincipal CustomUserDetails user,
                                                   @PathVariable Long id) {
        return ResponseEntity.ok(presupuestoService.get(user.getId(), id));
    }
}

package com.example.finanzas.controller;

import com.example.finanzas.dto.TransaccionRequest;
import com.example.finanzas.dto.TransaccionResponse;
import com.example.finanzas.entity.TransaccionTipo;
import com.example.finanzas.security.CustomUserDetails;
import com.example.finanzas.service.TransaccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transacciones")
@Tag(name = "Transacciones")
@SecurityRequirement(name = "bearerAuth")
public class TransaccionController {
    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @PostMapping
    @Operation(summary = "Registrar ingreso o gasto")
    public ResponseEntity<TransaccionResponse> create(@AuthenticationPrincipal CustomUserDetails user,
                                                      @Valid @RequestBody TransaccionRequest request) {
        return ResponseEntity.ok(transaccionService.create(user.getId(), request));
    }

    @GetMapping
    @Operation(summary = "Consultar historial y buscar transacciones")
    public ResponseEntity<List<TransaccionResponse>> history(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Long cuentaId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) TransaccionTipo tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaHasta,
            @RequestParam(required = false) String descripcion) {
        return ResponseEntity.ok(transaccionService.history(
                user.getId(), cuentaId, categoriaId, tipo, fechaDesde, fechaHasta, descripcion));
    }
}

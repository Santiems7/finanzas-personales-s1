package com.example.finanzas.controller;

import com.example.finanzas.dto.NotificacionResponse;
import com.example.finanzas.security.CustomUserDetails;
import com.example.finanzas.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones")
@SecurityRequirement(name = "bearerAuth")
public class NotificacionController {
    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    @Operation(summary = "Listar notificaciones del usuario")
    public ResponseEntity<List<NotificacionResponse>> listar(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(notificacionService.listar(user.getId()));
    }

    @PatchMapping("/{id}/leida")
    @Operation(summary = "Marcar notificación como leída")
    public ResponseEntity<NotificacionResponse> marcarLeida(@AuthenticationPrincipal CustomUserDetails user,
                                                            @PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarLeida(user.getId(), id));
    }
}

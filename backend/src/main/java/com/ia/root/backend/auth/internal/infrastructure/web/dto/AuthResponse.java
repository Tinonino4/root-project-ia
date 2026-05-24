package com.ia.root.backend.auth.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record AuthResponse(
    @Schema(description = "Token de acceso JWT")
    String token,
    
    @Schema(description = "Token de refresco")
    String refreshToken,
    
    @Schema(description = "ID del usuario")
    UUID id,
    
    @Schema(description = "Nombre del usuario")
    String name,
    
    @Schema(description = "Rol del usuario")
    String role
) {}

package com.ia.root.backend.auth.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MessageResponse(
    @Schema(description = "Mensaje de respuesta")
    String message
) {}

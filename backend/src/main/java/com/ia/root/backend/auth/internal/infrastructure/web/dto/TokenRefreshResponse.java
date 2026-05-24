package com.ia.root.backend.auth.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenRefreshResponse(
    @Schema(description = "Nuevo token de acceso JWT")
    String accessToken,

    @Schema(description = "Nuevo token de refresco")
    String refreshToken
) {}

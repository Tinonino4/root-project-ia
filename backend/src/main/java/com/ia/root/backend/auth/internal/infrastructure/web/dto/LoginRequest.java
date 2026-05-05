package com.ia.root.backend.auth.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Schema(description = "Correo electrónico", example = "juan@example.com")
    String email,

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña", example = "secret123")
    String password
) {}

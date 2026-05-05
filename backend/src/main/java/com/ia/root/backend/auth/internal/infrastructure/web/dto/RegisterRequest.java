package com.ia.root.backend.auth.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre completo del usuario", example = "Juan Perez")
    String name,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Schema(description = "Correo electrónico", example = "juan@example.com")
    String email,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(description = "Contraseña", example = "secret123")
    String password,

    @Schema(description = "Rol del usuario (ROLE_USER o ROLE_COMPANY)", example = "ROLE_USER")
    String role
) {}

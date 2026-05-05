package com.ia.root.backend.auth.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OtpVerificationRequest(
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Schema(description = "Correo electrónico", example = "juan@example.com")
    String email,

    @NotBlank(message = "El código es obligatorio")
    @Size(min = 6, max = 6, message = "El código debe tener 6 caracteres")
    @Schema(description = "Código OTP de 6 dígitos", example = "123456")
    String code
) {}

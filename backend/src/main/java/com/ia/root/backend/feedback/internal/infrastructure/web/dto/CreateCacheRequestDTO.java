package com.ia.root.backend.feedback.internal.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateCacheRequestDTO(
    @NotNull(message = "experienceId es obligatorio")
    UUID experienceId,

    @NotNull(message = "relationshipId es obligatorio")
    Integer relationshipId,

    @NotNull(message = "stillWorksThere es obligatorio")
    Boolean stillWorksThere,

    @NotBlank(message = "Nombre del referente es obligatorio")
    String targetName,

    @NotBlank(message = "Apellido del referente es obligatorio")
    String targetSurname,

    @NotBlank(message = "Email del referente es obligatorio")
    @Email(message = "El formato del email no es válido")
    String targetEmail,

    String targetPhone
) {}

package com.ia.root.backend.feedback.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Datos para crear una solicitud de feedback")
public record CreateCacheRequestDTO(
    @NotNull(message = "experienceId es obligatorio")
    @Schema(description = "ID de la experiencia laboral asociada", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID experienceId,

    @NotNull(message = "relationshipId es obligatorio")
    @Schema(description = "ID del tipo de relación (0=Jefe, 1=Compañero, 2=Subordinado, 3=Cliente, 4=Otro)", example = "1")
    Integer relationshipId,

    @NotNull(message = "stillWorksThere es obligatorio")
    @Schema(description = "Indica si el referente aún trabaja con el usuario", example = "true")
    Boolean stillWorksThere,

    @NotBlank(message = "Nombre del referente es obligatorio")
    @Schema(description = "Nombre del referente", example = "Ana")
    String targetName,

    @NotBlank(message = "Apellido del referente es obligatorio")
    @Schema(description = "Apellido del referente", example = "López")
    String targetSurname,

    @NotBlank(message = "Email del referente es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Schema(description = "Email del referente", example = "ana@empresa.com")
    String targetEmail,

    @Schema(description = "Teléfono del referente (opcional)", example = "+34612345678")
    String targetPhone
) {}

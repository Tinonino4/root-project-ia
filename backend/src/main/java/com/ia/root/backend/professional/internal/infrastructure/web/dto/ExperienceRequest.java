package com.ia.root.backend.professional.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "Datos de una experiencia laboral")
public record ExperienceRequest(
    @NotBlank(message = "Company name is required")
    @Schema(description = "Nombre de la empresa", example = "Acme Corp")
    String companyName,

    @Schema(description = "Departamento (opcional)", example = "Tecnología")
    String department,

    @NotBlank(message = "Position is required")
    @Schema(description = "Puesto o cargo", example = "Senior Developer")
    String position,

    @NotNull(message = "Start date is required")
    @Schema(description = "Fecha de inicio", example = "2022-01-15")
    LocalDate startDate,

    @Schema(description = "Fecha de fin (null si sigue trabajando)", example = "2024-06-30")
    LocalDate finishDate,

    @Schema(description = "Descripción de funciones (opcional)", example = "Desarrollo de APIs REST")
    String functions
) {}

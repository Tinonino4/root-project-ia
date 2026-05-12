package com.ia.root.backend.feedback.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "Vista del cuestionario de feedback con categorías y preguntas")
public record QuestionnaireViewDTO(
    @Schema(description = "ID de la solicitud de feedback") UUID cacheRequestId,
    @Schema(description = "ID del usuario evaluado") UUID userId,
    @Schema(description = "ID de la experiencia laboral") UUID experienceId,
    @Schema(description = "Categorías de skills con sus preguntas") List<CategoryDTO> categories
) {
    @Schema(description = "Categoría de skill con sus preguntas")
    public record CategoryDTO(
        @Schema(description = "ID de la categoría") UUID id,
        @Schema(description = "Código único", example = "TEAMWORK") String code,
        @Schema(description = "Nombre visible", example = "Trabajo en equipo") String name,
        @Schema(description = "Descripción de la categoría") String description,
        @Schema(description = "Preguntas ordenadas por posición") List<QuestionDTO> questions
    ) {}

    @Schema(description = "Pregunta individual de un skill")
    public record QuestionDTO(
        @Schema(description = "ID de la pregunta") UUID id,
        @Schema(description = "Texto de la pregunta") String text,
        @Schema(description = "Posición de la pregunta en la categoría", example = "1") int position
    ) {}
}

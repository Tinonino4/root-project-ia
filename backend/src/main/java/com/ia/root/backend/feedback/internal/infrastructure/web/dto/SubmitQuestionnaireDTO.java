package com.ia.root.backend.feedback.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Schema(description = "Datos para enviar las respuestas del cuestionario de feedback")
public record SubmitQuestionnaireDTO(
    @NotEmpty(message = "Las respuestas de skills son obligatorias")
    @Valid
    @Schema(description = "Lista de respuestas de rating (una por cada pregunta del cuestionario)")
    List<SkillAnswer> skillAnswers,

    @Schema(description = "Respuestas extra opcionales (preguntas abiertas, relación, etc.)",
        example = "{\"question1\": 3, \"question4\": \"Buen compañero\"}")
    Map<String, Object> extraAnswers
) {
    @Schema(description = "Respuesta individual a una pregunta de skill")
    public record SkillAnswer(
        @NotNull(message = "questionId es obligatorio")
        @Schema(description = "ID de la pregunta", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID questionId,

        @NotNull(message = "rating es obligatorio")
        @Min(value = 1, message = "El rating mínimo es 1")
        @Max(value = 5, message = "El rating máximo es 5")
        @Schema(description = "Puntuación del 1 al 5", example = "4", minimum = "1", maximum = "5")
        Integer rating
    ) {}
}

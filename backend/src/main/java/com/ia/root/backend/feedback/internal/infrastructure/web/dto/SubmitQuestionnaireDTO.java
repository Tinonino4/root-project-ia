package com.ia.root.backend.feedback.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Schema(description = "Datos para enviar las respuestas conductuales del cuestionario 360°")
public record SubmitQuestionnaireDTO(
    @NotEmpty(message = "Las respuestas conductuales son obligatorias")
    @Valid
    @Schema(description = "Lista de opciones seleccionadas por pregunta")
    List<BehavioralAnswer> answers,

    @Schema(description = "Comentarios u observaciones adicionales opcionales")
    String comments,

    @Schema(description = "Respuestas extra opcionales")
    Map<String, Object> extraAnswers
) {
    @Schema(description = "Respuesta individual a una pregunta conductual")
    public record BehavioralAnswer(
        @NotNull(message = "questionId es obligatorio")
        @Schema(description = "ID de la pregunta")
        UUID questionId,

        @NotNull(message = "selectedOptionId es obligatorio")
        @Schema(description = "ID de la opción elegida (o opciones elegidas)")
        List<UUID> selectedOptionIds
    ) {}
}

package com.ia.root.backend.feedback.internal.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SubmitQuestionnaireDTO(
    @NotEmpty(message = "Las respuestas de skills son obligatorias")
    @Valid
    List<SkillAnswer> skillAnswers,

    Map<String, Object> extraAnswers
) {
    public record SkillAnswer(
        @NotNull(message = "questionId es obligatorio")
        UUID questionId,

        @NotNull(message = "rating es obligatorio")
        @Min(value = 1, message = "El rating mínimo es 1")
        @Max(value = 5, message = "El rating máximo es 5")
        Integer rating
    ) {}
}

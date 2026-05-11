package com.ia.root.backend.feedback.internal.infrastructure.web.dto;

import java.util.List;
import java.util.UUID;

public record QuestionnaireViewDTO(
    UUID cacheRequestId,
    UUID userId,
    UUID experienceId,
    List<CategoryDTO> categories
) {
    public record CategoryDTO(
        UUID id,
        String code,
        String name,
        String description,
        List<QuestionDTO> questions
    ) {}

    public record QuestionDTO(
        UUID id,
        String text,
        int position
    ) {}
}

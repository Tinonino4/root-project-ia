package com.ia.root.backend.feedback.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "Vista del cuestionario conductual de feedback 360° por rol")
public record QuestionnaireViewDTO(
    @Schema(description = "ID de la solicitud de feedback") UUID cacheRequestId,
    @Schema(description = "ID del usuario evaluado") UUID userId,
    @Schema(description = "Nombre del usuario evaluado") String candidateName,
    @Schema(description = "ID de la experiencia laboral") UUID experienceId,
    @Schema(description = "Nombre de la empresa") String companyName,
    @Schema(description = "ID del tipo de relación") int relationshipTypeId,
    @Schema(description = "Código del tipo de relación (DIRECT_MANAGER, COLLEAGUE, SUBORDINATE)") String relationshipCode,
    @Schema(description = "Lista de 5 preguntas conductuales") List<BehavioralQuestionDTO> questions
) {
    @Schema(description = "Pregunta conductual individual")
    public record BehavioralQuestionDTO(
        @Schema(description = "ID de la pregunta") UUID id,
        @Schema(description = "Código único de la pregunta") String code,
        @Schema(description = "Tipo (BARS, FORCED_CHOICE, CULTURAL_FIT)") String type,
        @Schema(description = "Texto de la pregunta") String text,
        @Schema(description = "Posición") int position,
        @Schema(description = "Opciones disponibles") List<OptionDTO> options
    ) {}

    @Schema(description = "Opción de respuesta conductual")
    public record OptionDTO(
        @Schema(description = "ID de la opción") UUID id,
        @Schema(description = "Código de la opción") String code,
        @Schema(description = "Texto descriptivo de la opción") String text,
        @Schema(description = "Posición") int position
    ) {}
}

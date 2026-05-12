package com.ia.root.backend.feedback.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.UUID;

@Schema(description = "Vista resumida de una solicitud de feedback")
public record CacheRequestViewDTO(
    @Schema(description = "ID de la solicitud") UUID id,
    @Schema(description = "ID de la experiencia asociada") UUID experienceId,
    @Schema(description = "ID del tipo de relación profesional", example = "1") int relationshipId,
    @Schema(description = "Si el referente aún trabaja con el usuario") boolean stillWorksThere,
    @Schema(description = "Nombre del referente") String targetName,
    @Schema(description = "Apellido del referente") String targetSurname,
    @Schema(description = "Email del referente") String targetEmail,
    @Schema(description = "Teléfono del referente") String targetPhone,
    @Schema(description = "Si el cuestionario ya fue completado") boolean finished,
    @Schema(description = "Fecha de creación") ZonedDateTime createdAt
) {}

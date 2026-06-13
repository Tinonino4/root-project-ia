package com.ia.root.backend.feedback;

import java.time.ZonedDateTime;
import java.util.UUID;

public record TestimonialDTO(
    UUID experienceId,
    String comment,
    String relationshipCode,
    String trustLevel,
    int trustScore,
    String evaluatorName,
    String evaluatorSurname,
    ZonedDateTime createdAt
) {}

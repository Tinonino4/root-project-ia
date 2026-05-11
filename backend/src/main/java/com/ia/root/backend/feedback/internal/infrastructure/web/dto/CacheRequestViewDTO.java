package com.ia.root.backend.feedback.internal.infrastructure.web.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record CacheRequestViewDTO(
    UUID id,
    UUID experienceId,
    int relationshipId,
    boolean stillWorksThere,
    String targetName,
    String targetSurname,
    String targetEmail,
    String targetPhone,
    boolean finished,
    ZonedDateTime createdAt
) {}

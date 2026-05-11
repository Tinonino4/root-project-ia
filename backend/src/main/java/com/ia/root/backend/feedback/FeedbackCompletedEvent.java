package com.ia.root.backend.feedback;

import java.util.UUID;

public record FeedbackCompletedEvent(
    UUID cacheRequestId,
    UUID userId,
    UUID experienceId
) {}

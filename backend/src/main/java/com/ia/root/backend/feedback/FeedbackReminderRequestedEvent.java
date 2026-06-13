package com.ia.root.backend.feedback;

import java.util.UUID;

public record FeedbackReminderRequestedEvent(
    UUID cacheRequestId,
    UUID userId,
    String userName,
    String targetName,
    String targetSurname,
    String targetEmail,
    String companyName,
    String questionnaireUrl
) {}

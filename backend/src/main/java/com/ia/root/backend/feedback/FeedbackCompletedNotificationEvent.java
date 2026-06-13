package com.ia.root.backend.feedback;

public record FeedbackCompletedNotificationEvent(
    String candidateEmail,
    String candidateName,
    String refereeName,
    String companyName
) {}

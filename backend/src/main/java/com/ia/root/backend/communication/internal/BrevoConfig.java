package com.ia.root.backend.communication.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "brevo")
record BrevoConfig(
    String apiKey,
    boolean enabled,
    Sender sender,
    TemplateIds templateIds,
    String frontendBaseUrl
) {

    record Sender(String name, String email) {}

    record TemplateIds(
        long otpVerification,
        long passwordReset,
        long feedbackRequest,
        long feedbackReminder,
        long feedbackCompleted
    ) {}
}

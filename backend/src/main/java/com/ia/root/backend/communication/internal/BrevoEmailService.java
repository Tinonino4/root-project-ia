package com.ia.root.backend.communication.internal;

import brevo.ApiClient;
import brevo.Configuration;
import brevo.auth.ApiKeyAuth;
import brevoApi.TransactionalEmailsApi;
import brevoModel.SendSmtpEmail;
import brevoModel.SendSmtpEmailSender;
import brevoModel.SendSmtpEmailTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@EnableConfigurationProperties(BrevoConfig.class)
class BrevoEmailService {

    private static final Logger log = LoggerFactory.getLogger(BrevoEmailService.class);

    private final BrevoConfig config;
    private final TransactionalEmailsApi api;

    BrevoEmailService(BrevoConfig config) {
        this.config = config;

        ApiClient client = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKey = (ApiKeyAuth) client.getAuthentication("api-key");
        apiKey.setApiKey(config.apiKey());

        this.api = new TransactionalEmailsApi();
    }

    // ── OTP Verification ────────────────────────────────────

    void sendOtpVerification(String toEmail, String otpCode) {
        sendTemplate(
            config.templateIds().otpVerification(),
            toEmail,
            "Tu código para entrar en Caché 🔥",
            Map.of("OTP_CODE", otpCode)
        );
    }

    // ── Password Reset ──────────────────────────────────────

    void sendPasswordReset(String toEmail, String otpCode) {
        sendTemplate(
            config.templateIds().passwordReset(),
            toEmail,
            "Recupera tu acceso a Caché 🔑",
            Map.of("OTP_CODE", otpCode)
        );
    }

    // ── Feedback Request ────────────────────────────────────

    void sendFeedbackRequest(String toEmail, String targetName,
                             String userName, String companyName,
                             String questionnaireToken) {
        String questionnaireUrl = config.frontendBaseUrl() + "/questionnaire/" + questionnaireToken;

        sendTemplate(
            config.templateIds().feedbackRequest(),
            toEmail,
            userName + " confía en ti para algo importante",
            Map.of(
                "TARGET_NAME", targetName,
                "USER_NAME", userName,
                "COMPANY_NAME", companyName,
                "QUESTIONNAIRE_URL", questionnaireUrl
            )
        );
    }

    // ── Feedback Reminder ───────────────────────────────────

    void sendFeedbackReminder(String toEmail, String targetName,
                              String userName, String companyName,
                              String questionnaireToken) {
        String questionnaireUrl = config.frontendBaseUrl() + "/questionnaire/" + questionnaireToken;

        sendTemplate(
            config.templateIds().feedbackReminder(),
            toEmail,
            targetName + ", aún te espera una valoración pendiente 👀",
            Map.of(
                "TARGET_NAME", targetName,
                "USER_NAME", userName,
                "COMPANY_NAME", companyName,
                "QUESTIONNAIRE_URL", questionnaireUrl,
                "UNSUBSCRIBE_URL", "#"
            )
        );
    }

    // ── Core send logic ─────────────────────────────────────

    private void sendTemplate(long templateId, String toEmail,
                              String subject, Map<String, String> params) {
        if (!config.enabled()) {
            log.info("══════════════════════════════════════════════════════");
            log.info("📧 BREVO MOCK (enabled=false)");
            log.info("   To: {}", toEmail);
            log.info("   Subject: {}", subject);
            log.info("   Template ID: {}", templateId);
            log.info("   Params: {}", params);
            log.info("══════════════════════════════════════════════════════");
            return;
        }

        try {
            SendSmtpEmail email = new SendSmtpEmail();
            email.templateId(templateId);
            email.subject(subject);
            email.sender(new SendSmtpEmailSender()
                .name(config.sender().name())
                .email(config.sender().email()));
            email.to(List.of(new SendSmtpEmailTo().email(toEmail)));
            email.params(params);

            var response = api.sendTransacEmail(email);
            log.info("📧 Email sent via Brevo → to={}, templateId={}, messageId={}",
                toEmail, templateId, response.getMessageId());

        } catch (Exception e) {
            log.error("❌ Failed to send email via Brevo → to={}, templateId={}, error={}",
                toEmail, templateId, e.getMessage(), e);
            // No relanzamos la excepción para no romper el flujo del evento Modulith.
            // En producción se podría considerar un retry o dead-letter.
        }
    }
}

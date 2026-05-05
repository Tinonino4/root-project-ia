package com.ia.root.backend.communication.internal;

import com.ia.root.backend.auth.UserRequiresOtpEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
class EmailNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationListener.class);

    @ApplicationModuleListener
    void onUserRequiresOtp(UserRequiresOtpEvent event) {
        // En producción aquí iría la integración real con JavaMailSender, SendGrid, AWS SES, etc.
        log.info("==========================================================");
        log.info("MOCK EMAIL SERVICE (Modulith Async Event)");
        log.info("To: {}", event.email());
        log.info("Subject: Tu código de verificación");
        log.info("Body: Tu código OTP es: {}", event.otp());
        log.info("==========================================================");
    }
}

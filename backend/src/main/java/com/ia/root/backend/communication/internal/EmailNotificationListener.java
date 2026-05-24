package com.ia.root.backend.communication.internal;

import com.ia.root.backend.auth.UserRequiresOtpEvent;
import com.ia.root.backend.feedback.FeedbackRequestCreatedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
class EmailNotificationListener {

    private final BrevoEmailService brevoEmailService;

    EmailNotificationListener(BrevoEmailService brevoEmailService) {
        this.brevoEmailService = brevoEmailService;
    }

    @ApplicationModuleListener
    void onUserRequiresOtp(UserRequiresOtpEvent event) {
        switch (event.purpose()) {
            case ACCOUNT_VERIFICATION -> brevoEmailService.sendOtpVerification(event.email(), event.otp());
            case PASSWORD_RESET -> brevoEmailService.sendPasswordReset(event.email(), event.otp());
        }
    }

    @ApplicationModuleListener
    void onFeedbackRequestCreated(FeedbackRequestCreatedEvent event) {
        brevoEmailService.sendFeedbackRequest(
            event.targetEmail(),
            event.targetName(),
            event.userName(),
            event.companyName(),
            event.questionnaireUrl()
        );
    }
}

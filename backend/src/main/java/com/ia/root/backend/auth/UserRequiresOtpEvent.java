package com.ia.root.backend.auth;

import org.jspecify.annotations.NonNull;

public record UserRequiresOtpEvent(
    @NonNull String email,
    @NonNull String otp,
    @NonNull OtpPurpose purpose
) {

    public enum OtpPurpose {
        ACCOUNT_VERIFICATION,
        PASSWORD_RESET
    }
}

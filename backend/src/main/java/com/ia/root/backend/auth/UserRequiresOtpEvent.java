package com.ia.root.backend.auth;

import org.jspecify.annotations.NonNull;

public record UserRequiresOtpEvent(@NonNull String email, @NonNull String otp) {
}

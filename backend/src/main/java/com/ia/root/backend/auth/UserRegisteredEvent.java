package com.ia.root.backend.auth;

import java.util.UUID;

public record UserRegisteredEvent(
    UUID userId,
    String name,
    String email,
    String role
) {}

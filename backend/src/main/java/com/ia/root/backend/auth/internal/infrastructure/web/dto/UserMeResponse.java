package com.ia.root.backend.auth.internal.infrastructure.web.dto;

import java.util.UUID;

public record UserMeResponse(UUID id, String name, String email, String role) {}

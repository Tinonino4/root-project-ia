package com.ia.root.backend.professional.internal.infrastructure.web.dto;

import java.time.LocalDate;

public record UserProfileRequest(
    String name,
    String surname,
    String contactEmail,
    String aboutMe,
    String city,
    LocalDate birthday,
    String zipcode,
    String phoneNumber,
    String photoUrl,
    String jobTitle,
    String education
) {}

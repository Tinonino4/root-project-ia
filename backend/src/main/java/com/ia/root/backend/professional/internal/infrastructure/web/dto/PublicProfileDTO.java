package com.ia.root.backend.professional.internal.infrastructure.web.dto;

import com.ia.root.backend.analytics.SkillsData;
import com.ia.root.backend.professional.internal.domain.model.Experience;
import java.util.List;

public record PublicProfileDTO(
    String name,
    String surname,
    String jobTitle,
    String aboutMe,
    String photoUrl,
    List<Experience> experiences,
    SkillsData skills
) {
}

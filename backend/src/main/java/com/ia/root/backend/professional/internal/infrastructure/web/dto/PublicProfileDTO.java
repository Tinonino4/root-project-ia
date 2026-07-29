package com.ia.root.backend.professional.internal.infrastructure.web.dto;

import com.ia.root.backend.analytics.MultiLayerSkillsData;
import com.ia.root.backend.analytics.ArchetypeDataDTO;
import com.ia.root.backend.analytics.SkillsData;
import com.ia.root.backend.professional.internal.domain.model.Experience;
import com.ia.root.backend.feedback.ExperienceMetricsDTO;
import java.util.List;

public record PublicProfileDTO(
    java.util.UUID userId,
    String name,
    String surname,
    String jobTitle,
    String aboutMe,
    String photoUrl,
    String username,
    List<Experience> experiences,
    SkillsData skills,
    MultiLayerSkillsData skillsMultiLayer,
    ArchetypeDataDTO archetype,
    long totalReferencesCount,
    List<ExperienceMetricsDTO> experienceMetrics
) {
}

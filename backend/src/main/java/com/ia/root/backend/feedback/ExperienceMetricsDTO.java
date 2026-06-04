package com.ia.root.backend.feedback;

import java.util.Map;
import java.util.UUID;

public record ExperienceMetricsDTO(
    UUID experienceId,
    double averageScore,
    long referencesCount,
    Map<String, Double> categoryAverages,
    Map<String, Long> relationshipCounts,
    double averageTrustScore
) {}

package com.ia.root.backend.analytics;

import java.util.List;

public record ArchetypeDataDTO(
    List<String> tags,
    List<String> topStrengths,
    IdealEnvironmentDTO idealEnvironment
) {
    public record IdealEnvironmentDTO(
        String name,
        int fitPercentage
    ) {}
}

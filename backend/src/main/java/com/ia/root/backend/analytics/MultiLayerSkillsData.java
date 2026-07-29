package com.ia.root.backend.analytics;

public record MultiLayerSkillsData(
    SkillsData global,
    SkillsData managers,
    SkillsData peers,
    SkillsData subordinates
) {}

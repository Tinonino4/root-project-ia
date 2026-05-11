package com.ia.root.backend.analytics.internal.domain.repository;

import com.ia.root.backend.analytics.internal.domain.model.UserSkillsMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSkillsMetricsRepository extends JpaRepository<UserSkillsMetrics, UUID> {
    Optional<UserSkillsMetrics> findByUserId(UUID userId);
}

package com.ia.root.backend.feedback.internal.domain.repository;

import com.ia.root.backend.feedback.internal.domain.model.CacheRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CacheRequestRepository extends JpaRepository<CacheRequest, UUID> {
    Optional<CacheRequest> findByUrlToken(String urlToken);
    List<CacheRequest> findByUserId(UUID userId);
    List<CacheRequest> findByUserIdAndExperienceId(UUID userId, UUID experienceId);
    long countByUserIdAndExperienceIdAndFinishedTrue(UUID userId, UUID experienceId);
}

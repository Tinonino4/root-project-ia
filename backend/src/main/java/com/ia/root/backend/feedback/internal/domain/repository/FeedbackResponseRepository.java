package com.ia.root.backend.feedback.internal.domain.repository;

import com.ia.root.backend.feedback.internal.domain.model.FeedbackResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FeedbackResponseRepository extends JpaRepository<FeedbackResponse, UUID> {

    List<FeedbackResponse> findByCacheRequestId(UUID cacheRequestId);

    @Query("""
        SELECT fr FROM FeedbackResponse fr
        WHERE fr.cacheRequestId IN (
            SELECT cr.id FROM CacheRequest cr
            WHERE cr.userId = :userId AND cr.finished = true
        )
    """)
    List<FeedbackResponse> findAllCompletedByUserId(UUID userId);

    @Query("""
        SELECT fr FROM FeedbackResponse fr
        WHERE fr.cacheRequestId IN (
            SELECT cr.id FROM CacheRequest cr
            WHERE cr.userId = :userId AND cr.experienceId = :experienceId AND cr.finished = true
        )
    """)
    List<FeedbackResponse> findAllCompletedByUserIdAndExperienceId(UUID userId, UUID experienceId);
}

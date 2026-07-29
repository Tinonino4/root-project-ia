package com.ia.root.backend.feedback.internal.domain.repository;

import com.ia.root.backend.feedback.internal.domain.model.BehavioralResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BehavioralResponseRepository extends JpaRepository<BehavioralResponse, UUID> {
    List<BehavioralResponse> findByCacheRequestId(UUID cacheRequestId);
}

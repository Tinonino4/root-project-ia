package com.ia.root.backend.feedback.internal.domain.repository;

import com.ia.root.backend.feedback.internal.domain.model.BehavioralQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BehavioralQuestionRepository extends JpaRepository<BehavioralQuestion, UUID> {
    List<BehavioralQuestion> findByRelationshipTypeIdOrderByPositionAsc(int relationshipTypeId);
}

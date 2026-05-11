package com.ia.root.backend.feedback.internal.domain.repository;

import com.ia.root.backend.feedback.internal.domain.model.SkillQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SkillQuestionRepository extends JpaRepository<SkillQuestion, UUID> {
    List<SkillQuestion> findByCategoryIdOrderByPositionAsc(UUID categoryId);
}

package com.ia.root.backend.feedback.internal.domain.repository;

import com.ia.root.backend.feedback.internal.domain.model.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SkillCategoryRepository extends JpaRepository<SkillCategory, UUID> {
    List<SkillCategory> findAllByOrderByPositionAsc();
}

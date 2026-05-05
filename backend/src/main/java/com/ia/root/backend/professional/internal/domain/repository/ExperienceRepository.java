package com.ia.root.backend.professional.internal.domain.repository;

import com.ia.root.backend.professional.internal.domain.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, UUID> {
    List<Experience> findByUserIdOrderByStartDateDesc(UUID userId);
}

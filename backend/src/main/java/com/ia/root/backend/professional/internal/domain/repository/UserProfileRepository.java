package com.ia.root.backend.professional.internal.domain.repository;

import com.ia.root.backend.professional.internal.domain.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUserId(UUID userId);
    Optional<UserProfile> findByUsername(String username);
    boolean existsByUsername(String username);

    @org.springframework.data.jpa.repository.Query("""
        SELECT u FROM UserProfile u WHERE 
        LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR 
        LOWER(u.surname) LIKE LOWER(CONCAT('%', :query, '%')) OR 
        LOWER(u.jobTitle) LIKE LOWER(CONCAT('%', :query, '%')) OR 
        LOWER(u.city) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    java.util.List<UserProfile> searchByKeyword(@org.springframework.data.repository.query.Param("query") String query);
}

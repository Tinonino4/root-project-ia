package com.ia.root.backend.auth.internal.domain.repository;

import com.ia.root.backend.auth.internal.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser_Id(UUID userId);
    void deleteByToken(String token);
}

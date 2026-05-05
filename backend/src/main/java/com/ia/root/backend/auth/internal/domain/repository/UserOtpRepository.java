package com.ia.root.backend.auth.internal.domain.repository;
import com.ia.root.backend.auth.internal.domain.model.*;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserOtpRepository extends JpaRepository<UserOtp, UUID> {
    Optional<UserOtp> findByCodeAndUser_Email(String code, String email);
    void deleteByUser_Id(UUID userId);
}

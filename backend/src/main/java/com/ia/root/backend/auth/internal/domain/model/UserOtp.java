package com.ia.root.backend.auth.internal.domain.model;
import com.ia.root.backend.auth.internal.domain.model.User;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_otps")
public class UserOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private ZonedDateTime expiresAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    // Constructors
    public UserOtp() {}

    public UserOtp(String code, User user, ZonedDateTime expiresAt) {
        this.code = code;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ZonedDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(ZonedDateTime expiresAt) { this.expiresAt = expiresAt; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
}

package com.ia.root.backend.auth.internal.domain.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String passwordHash;

    @Column(nullable = false)
    private boolean isActive = false;

    @Column(nullable = false)
    private String provider = "LOCAL";

    private String providerId;

    @Column(nullable = false)
    private String role = "ROLE_USER";

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private ZonedDateTime updatedAt;

    protected User() {}

    public static User createLocal(String name, String email, String passwordHash, String role) {
        User user = new User();
        user.name = Objects.requireNonNull(name, "name must not be null");
        user.email = Objects.requireNonNull(email, "email must not be null");
        user.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        user.role = Objects.requireNonNull(role, "role must not be null");
        user.provider = "LOCAL";
        user.isActive = false;
        return user;
    }

    public static User createFromOAuth2(String name, String email, String provider, String providerId) {
        User user = new User();
        user.name = Objects.requireNonNull(name, "name must not be null");
        user.email = Objects.requireNonNull(email, "email must not be null");
        user.provider = Objects.requireNonNull(provider, "provider must not be null");
        user.providerId = providerId;
        user.role = "ROLE_USER";
        user.isActive = true;
        return user;
    }

    public void activate() {
        this.isActive = true;
    }

    public void linkOAuth2Provider(String provider, String providerId) {
        this.provider = Objects.requireNonNull(provider, "provider must not be null");
        this.providerId = providerId;
    }

    public void updatePassword(String passwordHash) {
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isActive() { return isActive; }
    public String getProvider() { return provider; }
    public String getProviderId() { return providerId; }
    public String getRole() { return role; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
}

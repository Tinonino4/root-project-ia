package com.ia.root.backend.feedback.internal.domain.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "cache_requests")
public class CacheRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "experience_id", nullable = false)
    private UUID experienceId;

    @Column(name = "relationship_id", nullable = false)
    private int relationshipId;

    @Column(name = "still_works_there", nullable = false)
    private boolean stillWorksThere;

    @Column(name = "target_name", nullable = false)
    private String targetName;

    @Column(name = "target_surname", nullable = false)
    private String targetSurname;

    @Column(name = "target_email", nullable = false)
    private String targetEmail;

    @Column(name = "url_token", nullable = false)
    private String urlToken;

    @Column(nullable = false)
    private boolean finished = false;

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible = false;

    @Column(name = "target_phone")
    private String targetPhone;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_answers", columnDefinition = "jsonb")
    private Map<String, Object> extraAnswers;

    @Column(name = "trust_score", nullable = false)
    private int trustScore = 0;

    @Column(name = "trust_level", nullable = false)
    private String trustLevel = "BASICO";

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    protected CacheRequest() {}

    public static CacheRequest create(UUID userId, UUID experienceId, int relationshipId,
                                       boolean stillWorksThere, String targetName,
                                       String targetSurname, String targetEmail,
                                       String targetPhone, String urlToken) {
        CacheRequest cr = new CacheRequest();
        cr.userId = Objects.requireNonNull(userId);
        cr.experienceId = Objects.requireNonNull(experienceId);
        cr.relationshipId = relationshipId;
        cr.stillWorksThere = stillWorksThere;
        cr.targetName = Objects.requireNonNull(targetName);
        cr.targetSurname = Objects.requireNonNull(targetSurname);
        cr.targetEmail = Objects.requireNonNull(targetEmail);
        cr.targetPhone = targetPhone;
        cr.urlToken = Objects.requireNonNull(urlToken);
        cr.finished = false;
        return cr;
    }

    public void markFinished(Map<String, Object> extraAnswers) {
        this.finished = true;
        this.extraAnswers = extraAnswers;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getExperienceId() { return experienceId; }
    public int getRelationshipId() { return relationshipId; }
    public boolean isStillWorksThere() { return stillWorksThere; }
    public String getTargetName() { return targetName; }
    public String getTargetSurname() { return targetSurname; }
    public String getTargetEmail() { return targetEmail; }
    public String getUrlToken() { return urlToken; }
    public boolean isFinished() { return finished; }
    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { this.isVisible = visible; }
    public String getTargetPhone() { return targetPhone; }
    public Map<String, Object> getExtraAnswers() { return extraAnswers; }
    public int getTrustScore() { return trustScore; }
    public void setTrustScore(int trustScore) { this.trustScore = trustScore; }
    public String getTrustLevel() { return trustLevel; }
    public void setTrustLevel(String trustLevel) { this.trustLevel = trustLevel; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
}

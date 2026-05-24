package com.ia.root.backend.analytics.internal.domain.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_skills_metrics")
public class UserSkillsMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private float teamwork;

    @Column(name = "self_confidence", nullable = false)
    private float selfConfidence;

    @Column(nullable = false)
    private float proactivity;

    @Column(nullable = false)
    private float integrity;

    @Column(nullable = false)
    private float flexibility;

    @Column(name = "average_score", nullable = false)
    private float averageScore;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private ZonedDateTime updatedAt;

    protected UserSkillsMetrics() {}

    public static UserSkillsMetrics createForUser(UUID userId) {
        UserSkillsMetrics m = new UserSkillsMetrics();
        m.userId = userId;
        return m;
    }

    public void recalculate(float teamwork, float selfConfidence, float proactivity,
                            float integrity, float flexibility) {
        this.teamwork = Math.round(teamwork * 100.0f) / 100.0f;
        this.selfConfidence = Math.round(selfConfidence * 100.0f) / 100.0f;
        this.proactivity = Math.round(proactivity * 100.0f) / 100.0f;
        this.integrity = Math.round(integrity * 100.0f) / 100.0f;
        this.flexibility = Math.round(flexibility * 100.0f) / 100.0f;
        float sum = this.teamwork + this.selfConfidence + this.proactivity + this.integrity + this.flexibility;
        this.averageScore = Math.round((sum / 5f) * 100.0f) / 100.0f;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public float getTeamwork() { return teamwork; }
    public float getSelfConfidence() { return selfConfidence; }
    public float getProactivity() { return proactivity; }
    public float getIntegrity() { return integrity; }
    public float getFlexibility() { return flexibility; }
    public float getAverageScore() { return averageScore; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
}

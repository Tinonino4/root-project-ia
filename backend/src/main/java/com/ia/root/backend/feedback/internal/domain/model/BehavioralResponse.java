package com.ia.root.backend.feedback.internal.domain.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "behavioral_responses")
public class BehavioralResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cache_request_id", nullable = false)
    private UUID cacheRequestId;

    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @Column(name = "selected_option_id", nullable = false)
    private UUID selectedOptionId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    protected BehavioralResponse() {}

    public static BehavioralResponse create(UUID cacheRequestId, UUID questionId, UUID selectedOptionId) {
        BehavioralResponse br = new BehavioralResponse();
        br.cacheRequestId = Objects.requireNonNull(cacheRequestId);
        br.questionId = Objects.requireNonNull(questionId);
        br.selectedOptionId = Objects.requireNonNull(selectedOptionId);
        return br;
    }

    public UUID getId() { return id; }
    public UUID getCacheRequestId() { return cacheRequestId; }
    public UUID getQuestionId() { return questionId; }
    public UUID getSelectedOptionId() { return selectedOptionId; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
}

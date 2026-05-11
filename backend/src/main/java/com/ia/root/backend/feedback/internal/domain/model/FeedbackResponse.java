package com.ia.root.backend.feedback.internal.domain.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "feedback_responses",
       uniqueConstraints = @UniqueConstraint(columnNames = {"cache_request_id", "question_id"}))
public class FeedbackResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cache_request_id", nullable = false)
    private UUID cacheRequestId;

    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @Column(nullable = false)
    private int rating;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    protected FeedbackResponse() {}

    public static FeedbackResponse create(UUID cacheRequestId, UUID questionId, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        FeedbackResponse fr = new FeedbackResponse();
        fr.cacheRequestId = Objects.requireNonNull(cacheRequestId);
        fr.questionId = Objects.requireNonNull(questionId);
        fr.rating = rating;
        return fr;
    }

    public UUID getId() { return id; }
    public UUID getCacheRequestId() { return cacheRequestId; }
    public UUID getQuestionId() { return questionId; }
    public int getRating() { return rating; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
}

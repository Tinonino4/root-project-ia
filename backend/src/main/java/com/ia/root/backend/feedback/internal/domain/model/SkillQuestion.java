package com.ia.root.backend.feedback.internal.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "skill_questions")
public class SkillQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private SkillCategory category;

    @Column(name = "question_text", nullable = false)
    private String questionText;

    @Column(nullable = false)
    private int position;

    protected SkillQuestion() {}

    public UUID getId() { return id; }
    public SkillCategory getCategory() { return category; }
    public String getQuestionText() { return questionText; }
    public int getPosition() { return position; }
}

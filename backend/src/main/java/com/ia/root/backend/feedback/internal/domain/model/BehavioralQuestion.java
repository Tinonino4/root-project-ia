package com.ia.root.backend.feedback.internal.domain.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "behavioral_questions")
public class BehavioralQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "relationship_type_id", nullable = false)
    private int relationshipTypeId;

    @Column(name = "question_code", nullable = false)
    private String questionCode;

    @Column(name = "question_type", nullable = false)
    private String questionType; // BARS, FORCED_CHOICE, CULTURAL_FIT

    @Column(name = "question_text", nullable = false)
    private String questionText;

    @Column(nullable = false)
    private int position;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("position ASC")
    private List<BehavioralQuestionOption> options = new ArrayList<>();

    protected BehavioralQuestion() {}

    public UUID getId() { return id; }
    public int getRelationshipTypeId() { return relationshipTypeId; }
    public String getQuestionCode() { return questionCode; }
    public String getQuestionType() { return questionType; }
    public String getQuestionText() { return questionText; }
    public int getPosition() { return position; }
    public List<BehavioralQuestionOption> getOptions() { return options; }
}

package com.ia.root.backend.feedback.internal.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "behavioral_question_options")
public class BehavioralQuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private BehavioralQuestion question;

    @Column(name = "option_code", nullable = false)
    private String optionCode;

    @Column(name = "option_text", nullable = false)
    private String optionText;

    @Column(name = "teamwork_pts", nullable = false)
    private int teamworkPts;

    @Column(name = "proactivity_pts", nullable = false)
    private int proactivityPts;

    @Column(name = "flexibility_pts", nullable = false)
    private int flexibilityPts;

    @Column(name = "integrity_pts", nullable = false)
    private int integrityPts;

    @Column(name = "leadership_pts", nullable = false)
    private int leadershipPts;

    @Column(nullable = false)
    private int position;

    protected BehavioralQuestionOption() {}

    public UUID getId() { return id; }
    public BehavioralQuestion getQuestion() { return question; }
    public String getOptionCode() { return optionCode; }
    public String getOptionText() { return optionText; }
    public int getTeamworkPts() { return teamworkPts; }
    public int getProactivityPts() { return proactivityPts; }
    public int getFlexibilityPts() { return flexibilityPts; }
    public int getIntegrityPts() { return integrityPts; }
    public int getLeadershipPts() { return leadershipPts; }
    public int getPosition() { return position; }
}

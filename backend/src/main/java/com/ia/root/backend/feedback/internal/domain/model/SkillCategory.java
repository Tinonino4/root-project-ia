package com.ia.root.backend.feedback.internal.domain.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "skill_categories")
public class SkillCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private int position;

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    @OrderBy("position ASC")
    private List<SkillQuestion> questions = new ArrayList<>();

    protected SkillCategory() {}

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPosition() { return position; }
    public List<SkillQuestion> getQuestions() { return questions; }
}

package com.ia.root.backend.feedback.internal.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "relationship_types")
public class RelationshipType {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private int position;

    protected RelationshipType() {}

    public Integer getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPosition() { return position; }
}

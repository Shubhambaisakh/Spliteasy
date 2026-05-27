package com.splitease.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Member> members = new ArrayList<>();

    public Group() {}

    public Group(Long id, String name, String description, LocalDateTime createdAt, List<Member> members) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.members = members;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Member> getMembers() { return members; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setMembers(List<Member> members) { this.members = members; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private LocalDateTime createdAt;
        private List<Member> members = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder members(List<Member> members) { this.members = members; return this; }

        public Group build() {
            Group g = new Group();
            g.id = this.id;
            g.name = this.name;
            g.description = this.description;
            g.createdAt = this.createdAt;
            g.members = this.members;
            return g;
        }
    }
}

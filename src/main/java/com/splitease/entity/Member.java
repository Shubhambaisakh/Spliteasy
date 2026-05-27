package com.splitease.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    public Member() {}

    @PrePersist
    protected void onCreate() {
        this.joinedAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = true;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Boolean getIsActive() { return isActive; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public Group getGroup() { return group; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
    public void setGroup(Group group) { this.group = group; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String email;
        private Boolean isActive = true;
        private LocalDateTime joinedAt;
        private Group group;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public Builder joinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; return this; }
        public Builder group(Group group) { this.group = group; return this; }

        public Member build() {
            Member m = new Member();
            m.id = this.id;
            m.name = this.name;
            m.email = this.email;
            m.isActive = this.isActive;
            m.joinedAt = this.joinedAt;
            m.group = this.group;
            return m;
        }
    }
}

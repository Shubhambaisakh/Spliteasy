package com.splitease.dto.response;

import java.time.LocalDateTime;

public class MemberResponse {
    private Long id;
    private String name;
    private String email;
    private Boolean isActive;
    private LocalDateTime joinedAt;

    public MemberResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String email;
        private Boolean isActive;
        private LocalDateTime joinedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public Builder joinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; return this; }

        public MemberResponse build() {
            MemberResponse r = new MemberResponse();
            r.id = this.id; r.name = this.name; r.email = this.email;
            r.isActive = this.isActive; r.joinedAt = this.joinedAt;
            return r;
        }
    }
}

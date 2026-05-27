package com.splitease.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class GroupResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private List<MemberResponse> members;

    public GroupResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<MemberResponse> getMembers() { return members; }
    public void setMembers(List<MemberResponse> members) { this.members = members; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private LocalDateTime createdAt;
        private List<MemberResponse> members;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder members(List<MemberResponse> members) { this.members = members; return this; }

        public GroupResponse build() {
            GroupResponse r = new GroupResponse();
            r.id = this.id; r.name = this.name; r.description = this.description;
            r.createdAt = this.createdAt; r.members = this.members;
            return r;
        }
    }
}

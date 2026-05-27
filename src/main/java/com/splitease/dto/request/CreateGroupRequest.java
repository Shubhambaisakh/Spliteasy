package com.splitease.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateGroupRequest {

    @NotBlank(message = "Group name must not be blank")
    private String name;

    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

package com.splitease.controller;

import com.splitease.dto.request.CreateGroupRequest;
import com.splitease.dto.response.GroupResponse;
import com.splitease.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
@Tag(name = "Groups", description = "Create and retrieve expense groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    @Operation(summary = "Create a new group")
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.createGroup(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a group by ID (includes all members)")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroup(id));
    }
}

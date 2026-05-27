package com.splitease.controller;

import com.splitease.dto.request.AddMemberRequest;
import com.splitease.dto.response.MemberResponse;
import com.splitease.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups/{groupId}/members")
@Tag(name = "Members", description = "Add and remove members from a group")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    @Operation(summary = "Add a member to a group")
    public ResponseEntity<MemberResponse> addMember(
            @PathVariable Long groupId,
            @Valid @RequestBody AddMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.addMember(groupId, request));
    }

    @DeleteMapping("/{memberId}")
    @Operation(summary = "Soft-delete a member (sets isActive = false)")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long memberId) {
        memberService.removeMember(groupId, memberId);
        return ResponseEntity.noContent().build();
    }
}

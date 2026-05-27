package com.splitease.service;

import com.splitease.dto.request.CreateGroupRequest;
import com.splitease.dto.response.GroupResponse;
import com.splitease.dto.response.MemberResponse;
import com.splitease.entity.Group;
import com.splitease.exception.GroupNotFoundException;
import com.splitease.repository.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request) {
        Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        Group saved = groupRepository.save(group);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));
        return toResponse(group);
    }

    public static GroupResponse toResponse(Group group) {
        List<MemberResponse> memberResponses = group.getMembers().stream()
                .map(m -> MemberResponse.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .email(m.getEmail())
                        .isActive(m.getIsActive())
                        .joinedAt(m.getJoinedAt())
                        .build())
                .collect(Collectors.toList());

        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .createdAt(group.getCreatedAt())
                .members(memberResponses)
                .build();
    }
}

package com.splitease.service;

import com.splitease.dto.request.AddMemberRequest;
import com.splitease.dto.response.MemberResponse;
import com.splitease.entity.Group;
import com.splitease.entity.Member;
import com.splitease.exception.GroupNotFoundException;
import com.splitease.exception.MemberNotFoundException;
import com.splitease.repository.GroupRepository;
import com.splitease.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;

    public MemberService(MemberRepository memberRepository, GroupRepository groupRepository) {
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public MemberResponse addMember(Long groupId, AddMemberRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        Member member = Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .isActive(true)
                .group(group)
                .build();

        Member saved = memberRepository.save(member);
        return toResponse(saved);
    }

    @Transactional
    public void removeMember(Long groupId, Long memberId) {
        Member member = memberRepository.findByIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(memberId, groupId));
        member.setIsActive(false);
        memberRepository.save(member);
    }

    public static MemberResponse toResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .isActive(member.getIsActive())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}

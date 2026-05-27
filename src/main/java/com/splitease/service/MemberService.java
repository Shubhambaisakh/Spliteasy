package com.splitease.service;

import com.splitease.dto.request.AddMemberRequest;
import com.splitease.dto.response.MemberResponse;
import com.splitease.entity.Group;
import com.splitease.entity.Member;
import com.splitease.exception.GroupNotFoundException;
import com.splitease.exception.MemberNotFoundException;
import com.splitease.repository.GroupRepository;
import com.splitease.repository.MemberRepository;
import com.splitease.repository.ExpenseRepository;
import com.splitease.repository.ExpenseSplitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;

    public MemberService(MemberRepository memberRepository,
                         GroupRepository groupRepository,
                         ExpenseRepository expenseRepository,
                         ExpenseSplitRepository expenseSplitRepository) {
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
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
        
        java.math.BigDecimal paid = expenseRepository.sumAmountPaidByMember(groupId, memberId);
        java.math.BigDecimal owed = expenseSplitRepository.sumAmountOwedByMember(groupId, memberId);

        if (paid.compareTo(java.math.BigDecimal.ZERO) == 0 && owed.compareTo(java.math.BigDecimal.ZERO) == 0) {
            memberRepository.delete(member);
        } else {
            member.setIsActive(false);
            memberRepository.save(member);
        }
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

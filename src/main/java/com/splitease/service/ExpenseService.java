package com.splitease.service;

import com.splitease.dto.request.CreateExpenseRequest;
import com.splitease.dto.request.CustomSplitEntry;
import com.splitease.dto.response.ExpenseResponse;
import com.splitease.dto.response.ExpenseSplitResponse;
import com.splitease.entity.*;
import com.splitease.exception.GroupNotFoundException;
import com.splitease.exception.InvalidSplitException;
import com.splitease.exception.MemberNotFoundException;
import com.splitease.repository.ExpenseRepository;
import com.splitease.repository.GroupRepository;
import com.splitease.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          GroupRepository groupRepository,
                          MemberRepository memberRepository) {
        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ExpenseResponse addExpense(Long groupId, CreateExpenseRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        Member paidBy = memberRepository.findByIdAndGroupId(request.getPaidByMemberId(), groupId)
                .orElseThrow(() -> new MemberNotFoundException(request.getPaidByMemberId(), groupId));

        Expense expense = Expense.builder()
                .description(request.getDescription())
                .amount(request.getAmount())
                .paidBy(paidBy)
                .group(group)
                .splitType(request.getSplitType())
                .splits(new ArrayList<>())
                .build();

        if (request.getSplitType() == SplitType.EQUAL) {
            buildEqualSplits(expense, groupId);
        } else {
            buildCustomSplits(expense, groupId, request);
        }

        Expense saved = expenseRepository.save(expense);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> listExpenses(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new GroupNotFoundException(groupId);
        }
        return expenseRepository.findByGroupIdOrderByCreatedAtDesc(groupId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void deleteExpense(Long groupId, Long expenseId) {
        if (!groupRepository.existsById(groupId)) {
            throw new GroupNotFoundException(groupId);
        }
        Expense expense = expenseRepository.findByIdAndGroupId(expenseId, groupId)
                .orElseThrow(() -> new InvalidSplitException(
                        "Expense " + expenseId + " not found in group " + groupId));
        expenseRepository.delete(expense);
    }

    private void buildEqualSplits(Expense expense, Long groupId) {
        List<Member> activeMembers = memberRepository.findByGroupIdAndIsActiveTrue(groupId);
        if (activeMembers.isEmpty()) {
            throw new InvalidSplitException("No active members in group " + groupId);
        }
        int count = activeMembers.size();
        BigDecimal share = expense.getAmount().divide(BigDecimal.valueOf(count), 2, RoundingMode.FLOOR);
        BigDecimal remainder = expense.getAmount().subtract(share.multiply(BigDecimal.valueOf(count)));

        for (int i = 0; i < activeMembers.size(); i++) {
            BigDecimal owed = (i == 0) ? share.add(remainder) : share;
            expense.getSplits().add(ExpenseSplit.builder()
                    .expense(expense)
                    .member(activeMembers.get(i))
                    .amountOwed(owed)
                    .build());
        }
    }

    private void buildCustomSplits(Expense expense, Long groupId, CreateExpenseRequest request) {
        List<CustomSplitEntry> customSplits = request.getCustomSplits();
        if (customSplits == null || customSplits.isEmpty()) {
            throw new InvalidSplitException("CUSTOM split type requires at least one split entry");
        }

        BigDecimal sum = customSplits.stream()
                .map(CustomSplitEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sum.compareTo(request.getAmount()) != 0) {
            throw new InvalidSplitException(
                    "Custom split amounts sum to " + sum + " but expense total is " + request.getAmount());
        }

        List<Member> groupMembers = memberRepository.findByGroupId(groupId);
        Map<Long, Member> memberMap = groupMembers.stream()
                .collect(Collectors.toMap(Member::getId, m -> m));

        for (CustomSplitEntry entry : customSplits) {
            Member member = memberMap.get(entry.getMemberId());
            if (member == null) {
                throw new MemberNotFoundException(entry.getMemberId(), groupId);
            }
            expense.getSplits().add(ExpenseSplit.builder()
                    .expense(expense)
                    .member(member)
                    .amountOwed(entry.getAmount())
                    .build());
        }
    }

    private ExpenseResponse toResponse(Expense expense) {
        List<ExpenseSplitResponse> splitResponses = expense.getSplits().stream()
                .map(s -> ExpenseSplitResponse.builder()
                        .memberId(s.getMember().getId())
                        .memberName(s.getMember().getName())
                        .amountOwed(s.getAmountOwed())
                        .build())
                .collect(Collectors.toList());

        return ExpenseResponse.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .paidByMemberId(expense.getPaidBy().getId())
                .paidByMemberName(expense.getPaidBy().getName())
                .splitType(expense.getSplitType())
                .createdAt(expense.getCreatedAt())
                .splits(splitResponses)
                .build();
    }
}

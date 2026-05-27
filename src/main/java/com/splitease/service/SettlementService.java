package com.splitease.service;

import com.splitease.dto.response.MemberBalanceResponse;
import com.splitease.dto.response.SettlementTransaction;
import com.splitease.entity.Member;
import com.splitease.exception.GroupNotFoundException;
import com.splitease.repository.ExpenseRepository;
import com.splitease.repository.ExpenseSplitRepository;
import com.splitease.repository.GroupRepository;
import com.splitease.repository.MemberRepository;
import com.splitease.util.DebtMinimizerUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SettlementService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final DebtMinimizerUtil debtMinimizerUtil;

    public SettlementService(GroupRepository groupRepository,
                             MemberRepository memberRepository,
                             ExpenseRepository expenseRepository,
                             ExpenseSplitRepository expenseSplitRepository,
                             DebtMinimizerUtil debtMinimizerUtil) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.debtMinimizerUtil = debtMinimizerUtil;
    }

    @Transactional(readOnly = true)
    public List<MemberBalanceResponse> getBalances(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new GroupNotFoundException(groupId);
        }

        List<Member> members = memberRepository.findByGroupId(groupId);
        List<MemberBalanceResponse> result = new ArrayList<>();

        for (Member member : members) {
            BigDecimal totalPaid = expenseRepository.sumAmountPaidByMember(groupId, member.getId());
            BigDecimal totalOwed = expenseSplitRepository.sumAmountOwedByMember(groupId, member.getId());
            BigDecimal netBalance = totalPaid.subtract(totalOwed);

            result.add(MemberBalanceResponse.builder()
                    .memberId(member.getId())
                    .memberName(member.getName())
                    .totalPaid(totalPaid)
                    .totalOwed(totalOwed)
                    .netBalance(netBalance)
                    .build());
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<SettlementTransaction> getSettlements(Long groupId) {
        List<MemberBalanceResponse> balances = getBalances(groupId);
        return debtMinimizerUtil.minimize(balances);
    }
}

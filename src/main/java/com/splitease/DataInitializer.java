package com.splitease;

import com.splitease.entity.*;
import com.splitease.repository.ExpenseRepository;
import com.splitease.repository.ExpenseSplitRepository;
import com.splitease.repository.GroupRepository;
import com.splitease.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;

    public DataInitializer(GroupRepository groupRepository,
                           MemberRepository memberRepository,
                           ExpenseRepository expenseRepository,
                           ExpenseSplitRepository expenseSplitRepository) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Cleanup any inactive members with 0 transactions on boot
        List<Member> allMembers = memberRepository.findAll();
        for (Member m : allMembers) {
            if (!m.getIsActive()) {
                BigDecimal paid = expenseRepository.sumAmountPaidByMember(m.getGroup().getId(), m.getId());
                BigDecimal owed = expenseSplitRepository.sumAmountOwedByMember(m.getGroup().getId(), m.getId());
                if (paid.compareTo(BigDecimal.ZERO) == 0 && owed.compareTo(BigDecimal.ZERO) == 0) {
                    memberRepository.delete(m);
                    log.info("DataInitializer: cleaned up inactive member '{}' with 0 transactions.", m.getName());
                }
            }
        }

        if (groupRepository.count() > 0) {
            log.info("DataInitializer: data already present, skipping seed.");
            return;
        }

        log.info("DataInitializer: seeding demo data...");

        Group group = Group.builder()
                .name("Goa Trip 2024")
                .description("Beach trip with college friends")
                .build();
        group = groupRepository.save(group);

        Member rahul = memberRepository.save(Member.builder()
                .name("Rahul").email("rahul@example.com").isActive(true).group(group).build());
        Member priya = memberRepository.save(Member.builder()
                .name("Priya").email("priya@example.com").isActive(true).group(group).build());
        Member amit = memberRepository.save(Member.builder()
                .name("Amit").email("amit@example.com").isActive(true).group(group).build());

        List<Member> all = List.of(rahul, priya, amit);

        createEqualExpense(group, rahul, "Dinner",    new BigDecimal("900.00"), all);
        createEqualExpense(group, priya, "Fuel",      new BigDecimal("600.00"), all);
        createEqualExpense(group, amit,  "Groceries", new BigDecimal("300.00"), all);

        log.info("DataInitializer: seeding complete. Group id = {}", group.getId());
    }

    private void createEqualExpense(Group group, Member paidBy, String description,
                                    BigDecimal amount, List<Member> members) {
        Expense expense = Expense.builder()
                .description(description)
                .amount(amount)
                .paidBy(paidBy)
                .group(group)
                .splitType(SplitType.EQUAL)
                .splits(new ArrayList<>())
                .build();

        int count = members.size();
        BigDecimal share = amount.divide(BigDecimal.valueOf(count), 2, RoundingMode.FLOOR);
        BigDecimal remainder = amount.subtract(share.multiply(BigDecimal.valueOf(count)));

        for (int i = 0; i < members.size(); i++) {
            BigDecimal owed = (i == 0) ? share.add(remainder) : share;
            expense.getSplits().add(ExpenseSplit.builder()
                    .expense(expense)
                    .member(members.get(i))
                    .amountOwed(owed)
                    .build());
        }

        expenseRepository.save(expense);
        log.info("  Created expense '{}' — {} paid by {}", description, amount, paidBy.getName());
    }
}

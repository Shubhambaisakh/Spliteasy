package com.splitease.util;

import com.splitease.dto.response.MemberBalanceResponse;
import com.splitease.dto.response.SettlementTransaction;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DebtMinimizerUtilTest {

    private final DebtMinimizerUtil minimizer = new DebtMinimizerUtil();

    @Test
    void testMinimizeThreeMembersGoaTripData() {
        // Rahul: net = +300.00
        MemberBalanceResponse rahul = MemberBalanceResponse.builder()
                .memberId(1L).memberName("Rahul")
                .totalPaid(new BigDecimal("900.00")).totalOwed(new BigDecimal("600.00"))
                .netBalance(new BigDecimal("300.00")).build();

        // Priya: net = 0.00
        MemberBalanceResponse priya = MemberBalanceResponse.builder()
                .memberId(2L).memberName("Priya")
                .totalPaid(new BigDecimal("600.00")).totalOwed(new BigDecimal("600.00"))
                .netBalance(new BigDecimal("0.00")).build();

        // Amit: net = -300.00
        MemberBalanceResponse amit = MemberBalanceResponse.builder()
                .memberId(3L).memberName("Amit")
                .totalPaid(new BigDecimal("300.00")).totalOwed(new BigDecimal("600.00"))
                .netBalance(new BigDecimal("-300.00")).build();

        List<MemberBalanceResponse> balances = Arrays.asList(rahul, priya, amit);
        List<SettlementTransaction> transactions = minimizer.minimize(balances);

        // Amit should pay Rahul 300.00, Priya does not participate.
        assertEquals(1, transactions.size());
        
        SettlementTransaction t = transactions.get(0);
        assertEquals(3L, t.getFromMemberId());
        assertEquals("Amit", t.getFromMemberName());
        assertEquals(1L, t.getToMemberId());
        assertEquals("Rahul", t.getToMemberName());
        assertEquals(0, t.getAmount().compareTo(new BigDecimal("300.00")));
    }

    @Test
    void testMinimizeMultipleTransactions() {
        // Member A: +400.00
        MemberBalanceResponse memberA = MemberBalanceResponse.builder()
                .memberId(1L).memberName("Member A")
                .netBalance(new BigDecimal("400.00")).build();

        // Member B: +100.00
        MemberBalanceResponse memberB = MemberBalanceResponse.builder()
                .memberId(2L).memberName("Member B")
                .netBalance(new BigDecimal("100.00")).build();

        // Member C: -250.00
        MemberBalanceResponse memberC = MemberBalanceResponse.builder()
                .memberId(3L).memberName("Member C")
                .netBalance(new BigDecimal("-250.00")).build();

        // Member D: -250.00
        MemberBalanceResponse memberD = MemberBalanceResponse.builder()
                .memberId(4L).memberName("Member D")
                .netBalance(new BigDecimal("-250.00")).build();

        List<MemberBalanceResponse> balances = Arrays.asList(memberA, memberB, memberC, memberD);
        List<SettlementTransaction> transactions = minimizer.minimize(balances);

        // Optimal settlements should sum up to the total debt (500)
        BigDecimal totalSettled = BigDecimal.ZERO;
        for (SettlementTransaction t : transactions) {
            totalSettled = totalSettled.add(t.getAmount());
        }

        assertEquals(0, totalSettled.compareTo(new BigDecimal("500.00")));
        // Should require at most 3 transactions (N-1)
        assertTrue(transactions.size() <= 3);
    }

    @Test
    void testMinimizeFullySettled() {
        MemberBalanceResponse rahul = MemberBalanceResponse.builder()
                .memberId(1L).memberName("Rahul")
                .netBalance(new BigDecimal("0.00")).build();

        MemberBalanceResponse amit = MemberBalanceResponse.builder()
                .memberId(2L).memberName("Amit")
                .netBalance(new BigDecimal("0.00")).build();

        List<MemberBalanceResponse> balances = Arrays.asList(rahul, amit);
        List<SettlementTransaction> transactions = minimizer.minimize(balances);

        // No transactions needed
        assertTrue(transactions.isEmpty());
    }
}

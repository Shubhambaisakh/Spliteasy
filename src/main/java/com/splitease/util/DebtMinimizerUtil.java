package com.splitease.util;

import com.splitease.dto.response.MemberBalanceResponse;
import com.splitease.dto.response.SettlementTransaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Greedy debt-minimization algorithm.
 *
 * <p>Algorithm overview:
 * <ol>
 *   <li>Separate members into creditors (net balance > 0) and debtors (net balance < 0).</li>
 *   <li>Use max-heaps so we always process the largest outstanding amounts first.</li>
 *   <li>Each iteration: pick the biggest creditor and biggest debtor, settle
 *       min(creditor.balance, |debtor.balance|), record the transaction, and
 *       re-enqueue whichever side still has a remainder.</li>
 *   <li>Repeat until all balances are zero.</li>
 * </ol>
 *
 * <p>Example (from the data initializer):
 * <pre>
 *   Rahul paid 900, owes 600  → net = +300
 *   Priya paid 600, owes 600  → net =   0
 *   Amit  paid 300, owes 600  → net = -300
 *
 *   Result: Amit pays Rahul ₹300  (1 transaction instead of potentially 2)
 * </pre>
 */
@Component
public class DebtMinimizerUtil {

    private static final BigDecimal EPSILON = new BigDecimal("0.01");

    /**
     * Computes the minimum set of transactions needed to settle all debts.
     *
     * @param balances list of net balances per member (from SettlementService)
     * @return ordered list of settlement transactions
     */
    public List<SettlementTransaction> minimize(List<MemberBalanceResponse> balances) {
        // Max-heap for creditors: highest balance first
        PriorityQueue<MemberBalanceResponse> creditors = new PriorityQueue<>(
                Comparator.comparing(MemberBalanceResponse::getNetBalance).reversed()
        );

        // Max-heap for debtors: largest absolute debt first
        PriorityQueue<MemberBalanceResponse> debtors = new PriorityQueue<>(
                Comparator.comparing((MemberBalanceResponse m) -> m.getNetBalance().abs()).reversed()
        );

        for (MemberBalanceResponse b : balances) {
            int cmp = b.getNetBalance().compareTo(EPSILON);
            if (cmp > 0) {
                creditors.offer(b);
            } else if (b.getNetBalance().compareTo(EPSILON.negate()) < 0) {
                debtors.offer(b);
            }
            // Members with balance ~0 are already settled — skip
        }

        List<SettlementTransaction> transactions = new ArrayList<>();

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            MemberBalanceResponse creditor = creditors.poll();
            MemberBalanceResponse debtor   = debtors.poll();

            BigDecimal creditAmt = creditor.getNetBalance();
            BigDecimal debtAmt   = debtor.getNetBalance().abs();

            BigDecimal settled = creditAmt.min(debtAmt).setScale(2, RoundingMode.HALF_UP);

            transactions.add(SettlementTransaction.builder()
                    .fromMemberId(debtor.getMemberId())
                    .fromMemberName(debtor.getMemberName())
                    .toMemberId(creditor.getMemberId())
                    .toMemberName(creditor.getMemberName())
                    .amount(settled)
                    .build());

            BigDecimal remainingCredit = creditAmt.subtract(settled).setScale(2, RoundingMode.HALF_UP);
            BigDecimal remainingDebt   = debtAmt.subtract(settled).setScale(2, RoundingMode.HALF_UP);

            if (remainingCredit.compareTo(EPSILON) > 0) {
                creditor.setNetBalance(remainingCredit);
                creditors.offer(creditor);
            }

            if (remainingDebt.compareTo(EPSILON) > 0) {
                debtor.setNetBalance(remainingDebt.negate());
                debtors.offer(debtor);
            }
        }

        return transactions;
    }
}

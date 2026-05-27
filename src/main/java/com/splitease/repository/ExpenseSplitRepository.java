package com.splitease.repository;

import com.splitease.entity.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {

    /**
     * Sum of all amounts owed by a specific member across all expenses in a group.
     */
    @Query("SELECT COALESCE(SUM(es.amountOwed), 0) FROM ExpenseSplit es " +
           "WHERE es.expense.group.id = :groupId AND es.member.id = :memberId")
    BigDecimal sumAmountOwedByMember(@Param("groupId") Long groupId, @Param("memberId") Long memberId);
}

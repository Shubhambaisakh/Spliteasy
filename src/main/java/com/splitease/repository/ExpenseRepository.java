package com.splitease.repository;

import com.splitease.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByGroupIdOrderByCreatedAtDesc(Long groupId);

    Optional<Expense> findByIdAndGroupId(Long id, Long groupId);

    /**
     * Sum of all amounts paid by a specific member in a group.
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.group.id = :groupId AND e.paidBy.id = :memberId")
    java.math.BigDecimal sumAmountPaidByMember(@Param("groupId") Long groupId, @Param("memberId") Long memberId);
}

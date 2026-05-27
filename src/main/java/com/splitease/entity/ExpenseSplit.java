package com.splitease.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "expense_splits")
public class ExpenseSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountOwed;

    public ExpenseSplit() {}

    // Getters
    public Long getId() { return id; }
    public Expense getExpense() { return expense; }
    public Member getMember() { return member; }
    public BigDecimal getAmountOwed() { return amountOwed; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setExpense(Expense expense) { this.expense = expense; }
    public void setMember(Member member) { this.member = member; }
    public void setAmountOwed(BigDecimal amountOwed) { this.amountOwed = amountOwed; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Expense expense;
        private Member member;
        private BigDecimal amountOwed;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder expense(Expense expense) { this.expense = expense; return this; }
        public Builder member(Member member) { this.member = member; return this; }
        public Builder amountOwed(BigDecimal amountOwed) { this.amountOwed = amountOwed; return this; }

        public ExpenseSplit build() {
            ExpenseSplit s = new ExpenseSplit();
            s.id = this.id;
            s.expense = this.expense;
            s.member = this.member;
            s.amountOwed = this.amountOwed;
            return s;
        }
    }
}

package com.splitease.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_member_id", nullable = false)
    private Member paidBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SplitType splitType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ExpenseSplit> splits = new ArrayList<>();

    public Expense() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public Member getPaidBy() { return paidBy; }
    public Group getGroup() { return group; }
    public SplitType getSplitType() { return splitType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<ExpenseSplit> getSplits() { return splits; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPaidBy(Member paidBy) { this.paidBy = paidBy; }
    public void setGroup(Group group) { this.group = group; }
    public void setSplitType(SplitType splitType) { this.splitType = splitType; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setSplits(List<ExpenseSplit> splits) { this.splits = splits; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String description;
        private BigDecimal amount;
        private Member paidBy;
        private Group group;
        private SplitType splitType;
        private LocalDateTime createdAt;
        private List<ExpenseSplit> splits = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder paidBy(Member paidBy) { this.paidBy = paidBy; return this; }
        public Builder group(Group group) { this.group = group; return this; }
        public Builder splitType(SplitType splitType) { this.splitType = splitType; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder splits(List<ExpenseSplit> splits) { this.splits = splits; return this; }

        public Expense build() {
            Expense e = new Expense();
            e.id = this.id;
            e.description = this.description;
            e.amount = this.amount;
            e.paidBy = this.paidBy;
            e.group = this.group;
            e.splitType = this.splitType;
            e.createdAt = this.createdAt;
            e.splits = this.splits;
            return e;
        }
    }
}

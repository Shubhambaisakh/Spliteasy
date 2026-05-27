package com.splitease.dto.response;

import com.splitease.entity.SplitType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ExpenseResponse {
    private Long id;
    private String description;
    private BigDecimal amount;
    private Long paidByMemberId;
    private String paidByMemberName;
    private SplitType splitType;
    private LocalDateTime createdAt;
    private List<ExpenseSplitResponse> splits;

    public ExpenseResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Long getPaidByMemberId() { return paidByMemberId; }
    public void setPaidByMemberId(Long paidByMemberId) { this.paidByMemberId = paidByMemberId; }
    public String getPaidByMemberName() { return paidByMemberName; }
    public void setPaidByMemberName(String paidByMemberName) { this.paidByMemberName = paidByMemberName; }
    public SplitType getSplitType() { return splitType; }
    public void setSplitType(SplitType splitType) { this.splitType = splitType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<ExpenseSplitResponse> getSplits() { return splits; }
    public void setSplits(List<ExpenseSplitResponse> splits) { this.splits = splits; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String description;
        private BigDecimal amount;
        private Long paidByMemberId;
        private String paidByMemberName;
        private SplitType splitType;
        private LocalDateTime createdAt;
        private List<ExpenseSplitResponse> splits;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder description(String d) { this.description = d; return this; }
        public Builder amount(BigDecimal a) { this.amount = a; return this; }
        public Builder paidByMemberId(Long id) { this.paidByMemberId = id; return this; }
        public Builder paidByMemberName(String n) { this.paidByMemberName = n; return this; }
        public Builder splitType(SplitType t) { this.splitType = t; return this; }
        public Builder createdAt(LocalDateTime t) { this.createdAt = t; return this; }
        public Builder splits(List<ExpenseSplitResponse> s) { this.splits = s; return this; }

        public ExpenseResponse build() {
            ExpenseResponse r = new ExpenseResponse();
            r.id = this.id; r.description = this.description; r.amount = this.amount;
            r.paidByMemberId = this.paidByMemberId; r.paidByMemberName = this.paidByMemberName;
            r.splitType = this.splitType; r.createdAt = this.createdAt; r.splits = this.splits;
            return r;
        }
    }
}

package com.splitease.dto.response;

import java.math.BigDecimal;

public class SettlementTransaction {
    private Long fromMemberId;
    private String fromMemberName;
    private Long toMemberId;
    private String toMemberName;
    private BigDecimal amount;

    public SettlementTransaction() {}

    public Long getFromMemberId() { return fromMemberId; }
    public void setFromMemberId(Long fromMemberId) { this.fromMemberId = fromMemberId; }
    public String getFromMemberName() { return fromMemberName; }
    public void setFromMemberName(String fromMemberName) { this.fromMemberName = fromMemberName; }
    public Long getToMemberId() { return toMemberId; }
    public void setToMemberId(Long toMemberId) { this.toMemberId = toMemberId; }
    public String getToMemberName() { return toMemberName; }
    public void setToMemberName(String toMemberName) { this.toMemberName = toMemberName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long fromMemberId;
        private String fromMemberName;
        private Long toMemberId;
        private String toMemberName;
        private BigDecimal amount;

        public Builder fromMemberId(Long id) { this.fromMemberId = id; return this; }
        public Builder fromMemberName(String n) { this.fromMemberName = n; return this; }
        public Builder toMemberId(Long id) { this.toMemberId = id; return this; }
        public Builder toMemberName(String n) { this.toMemberName = n; return this; }
        public Builder amount(BigDecimal a) { this.amount = a; return this; }

        public SettlementTransaction build() {
            SettlementTransaction t = new SettlementTransaction();
            t.fromMemberId = this.fromMemberId; t.fromMemberName = this.fromMemberName;
            t.toMemberId = this.toMemberId; t.toMemberName = this.toMemberName;
            t.amount = this.amount;
            return t;
        }
    }
}

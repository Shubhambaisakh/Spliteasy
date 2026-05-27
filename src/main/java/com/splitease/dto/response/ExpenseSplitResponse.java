package com.splitease.dto.response;

import java.math.BigDecimal;

public class ExpenseSplitResponse {
    private Long memberId;
    private String memberName;
    private BigDecimal amountOwed;

    public ExpenseSplitResponse() {}

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public BigDecimal getAmountOwed() { return amountOwed; }
    public void setAmountOwed(BigDecimal amountOwed) { this.amountOwed = amountOwed; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long memberId;
        private String memberName;
        private BigDecimal amountOwed;

        public Builder memberId(Long memberId) { this.memberId = memberId; return this; }
        public Builder memberName(String memberName) { this.memberName = memberName; return this; }
        public Builder amountOwed(BigDecimal amountOwed) { this.amountOwed = amountOwed; return this; }

        public ExpenseSplitResponse build() {
            ExpenseSplitResponse r = new ExpenseSplitResponse();
            r.memberId = this.memberId; r.memberName = this.memberName; r.amountOwed = this.amountOwed;
            return r;
        }
    }
}

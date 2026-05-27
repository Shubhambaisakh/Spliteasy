package com.splitease.dto.response;

import java.math.BigDecimal;

public class MemberBalanceResponse {
    private Long memberId;
    private String memberName;
    private BigDecimal totalPaid;
    private BigDecimal totalOwed;
    private BigDecimal netBalance;

    public MemberBalanceResponse() {}

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }
    public BigDecimal getTotalOwed() { return totalOwed; }
    public void setTotalOwed(BigDecimal totalOwed) { this.totalOwed = totalOwed; }
    public BigDecimal getNetBalance() { return netBalance; }
    public void setNetBalance(BigDecimal netBalance) { this.netBalance = netBalance; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long memberId;
        private String memberName;
        private BigDecimal totalPaid;
        private BigDecimal totalOwed;
        private BigDecimal netBalance;

        public Builder memberId(Long memberId) { this.memberId = memberId; return this; }
        public Builder memberName(String memberName) { this.memberName = memberName; return this; }
        public Builder totalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; return this; }
        public Builder totalOwed(BigDecimal totalOwed) { this.totalOwed = totalOwed; return this; }
        public Builder netBalance(BigDecimal netBalance) { this.netBalance = netBalance; return this; }

        public MemberBalanceResponse build() {
            MemberBalanceResponse r = new MemberBalanceResponse();
            r.memberId = this.memberId; r.memberName = this.memberName;
            r.totalPaid = this.totalPaid; r.totalOwed = this.totalOwed;
            r.netBalance = this.netBalance;
            return r;
        }
    }
}

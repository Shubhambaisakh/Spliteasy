package com.splitease.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CustomSplitEntry {

    @NotNull(message = "Member ID must not be null")
    private Long memberId;

    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "0.01", message = "Split amount must be greater than zero")
    private BigDecimal amount;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}

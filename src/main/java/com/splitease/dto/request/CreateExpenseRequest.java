package com.splitease.dto.request;

import com.splitease.entity.SplitType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class CreateExpenseRequest {

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "paidBy member ID must not be null")
    private Long paidByMemberId;

    @NotNull(message = "Split type must not be null")
    private SplitType splitType;

    @Valid
    private List<CustomSplitEntry> customSplits;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Long getPaidByMemberId() { return paidByMemberId; }
    public void setPaidByMemberId(Long paidByMemberId) { this.paidByMemberId = paidByMemberId; }
    public SplitType getSplitType() { return splitType; }
    public void setSplitType(SplitType splitType) { this.splitType = splitType; }
    public List<CustomSplitEntry> getCustomSplits() { return customSplits; }
    public void setCustomSplits(List<CustomSplitEntry> customSplits) { this.customSplits = customSplits; }
}

package com.splitease.controller;

import com.splitease.dto.response.MemberBalanceResponse;
import com.splitease.dto.response.SettlementTransaction;
import com.splitease.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups/{groupId}")
@Tag(name = "Settlements", description = "View balances and minimized settlement transactions")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping("/balances")
    @Operation(summary = "Get net balance for each member in the group")
    public ResponseEntity<List<MemberBalanceResponse>> getBalances(@PathVariable Long groupId) {
        return ResponseEntity.ok(settlementService.getBalances(groupId));
    }

    @GetMapping("/settlements")
    @Operation(summary = "Get minimized settlement transactions to clear all debts")
    public ResponseEntity<List<SettlementTransaction>> getSettlements(@PathVariable Long groupId) {
        return ResponseEntity.ok(settlementService.getSettlements(groupId));
    }
}

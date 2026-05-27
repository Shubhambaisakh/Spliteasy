package com.splitease.controller;

import com.splitease.dto.request.CreateExpenseRequest;
import com.splitease.dto.response.ExpenseResponse;
import com.splitease.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups/{groupId}/expenses")
@Tag(name = "Expenses", description = "Manage expenses within a group")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @Operation(summary = "Add an expense to a group")
    public ResponseEntity<ExpenseResponse> addExpense(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.addExpense(groupId, request));
    }

    @GetMapping
    @Operation(summary = "List all expenses in a group (newest first)")
    public ResponseEntity<List<ExpenseResponse>> listExpenses(@PathVariable Long groupId) {
        return ResponseEntity.ok(expenseService.listExpenses(groupId));
    }

    @DeleteMapping("/{expenseId}")
    @Operation(summary = "Delete an expense (also deletes its splits)")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long groupId,
            @PathVariable Long expenseId) {
        expenseService.deleteExpense(groupId, expenseId);
        return ResponseEntity.noContent().build();
    }
}

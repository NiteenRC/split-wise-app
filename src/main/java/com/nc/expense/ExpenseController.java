package com.nc.expense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/{expenseId}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long expenseId) {
        Expense expense = expenseService.getExpenseById(expenseId);
        return ResponseEntity.ok().body(expense);
    }

    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
        Expense createdExpense = expenseService.saveOrUpdateExpense(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }

    @PutMapping
    public ResponseEntity<Expense> updateExpense(@RequestBody Expense expenseDetails) {
        Expense updatedExpense = expenseService.saveOrUpdateExpense(expenseDetails);
        return ResponseEntity.ok().body(updatedExpense);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.ok().build();
    }
}


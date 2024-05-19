package com.nc.expense;

import com.nc.model.ExpenseDTO;
import com.nc.model.UserExpenseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<?> getAllExpense() {
        List<ExpenseDTO> expense = expenseService.getAllExpenses();
        return ResponseEntity.ok().body(expense);
    }

    @PostMapping
    public ResponseEntity<?> createExpense(@RequestParam("splitType") String splitType, @RequestBody UserExpenseModel userExpenseModel) {
        List<Expense> createdExpense = expenseService.saveOrUpdateExpense(userExpenseModel, splitType);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }
}


package com.nc.expense;

import com.nc.utility.HttpResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {
    private ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<?> getAllExpense() {
        List<ExpenseDTO> expense = expenseService.getAllExpenses();
        return ResponseEntity.ok().body(expense);
    }

    @PostMapping
    public ResponseEntity<?> createExpense(@RequestBody ExpenseRequest expenseRequest) {
        List<ExpenseDTO> createdExpenses = expenseService.saveOrUpdate(expenseRequest);
        HttpResponse response = new HttpResponse("success", createdExpenses, "Expense created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}


package com.nc.expenseDetails;

import com.nc.expense.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseDetailsRepository extends JpaRepository<ExpenseDetails, Long> {
    List<ExpenseDetails> findByExpense(Expense expense);
}

package com.nc.expense;

import com.nc.utility.SplitType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    boolean existsByExpenseName(String expenseName);

    Optional<Expense> findByExpenseName(String expenseName);

    Optional<Expense> findByExpenseNameAndSplitType(String expenseName, SplitType splitType);
}

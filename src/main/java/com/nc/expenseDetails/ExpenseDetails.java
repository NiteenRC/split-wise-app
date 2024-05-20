package com.nc.expenseDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nc.expense.Expense;
import com.nc.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ExpenseDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amountPaid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id")
    @JsonBackReference
    private Expense expense;
    @ManyToOne(fetch = FetchType.LAZY)
    private User payer;
}

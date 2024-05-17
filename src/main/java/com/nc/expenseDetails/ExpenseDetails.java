package com.nc.expenseDetails;

import com.nc.expense.Expense;
import com.nc.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ExpenseDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "expense_id")
    private Expense expense;
    @ManyToOne
    private User payer;
    private Double amountPaid;
}

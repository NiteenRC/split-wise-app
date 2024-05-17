package com.nc.model;

import com.nc.expense.Expense;
import com.nc.user.User;
import lombok.Data;

import java.util.List;

@Data
public class ExpenseModel {
    private Expense expense;
    private Double amountPaid;
    private User payer;
    private List<User> splitBetweenUsers;
}

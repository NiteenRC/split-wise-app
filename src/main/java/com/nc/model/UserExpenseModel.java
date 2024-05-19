package com.nc.model;

import lombok.Data;

import java.util.List;

@Data
public class UserExpenseModel {
    private ExpenseModel expenseModel;
    private List<UserModel> payers;
}
package com.nc.expense;

import com.nc.utility.SplitType;
import lombok.Data;

import java.util.List;

@Data
public class ExpenseRequest {
    private String expenseName;
    private String expenseType;
    private double expenseAmount;
    private double userAmountPaid;
    private Long payer;
    private List<Long> splitBetweenUserIds;
    //private List<UserRequest> payers;
    private SplitType splitType;
    private Long groupId;
}

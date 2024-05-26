package com.nc.expense;

import com.nc.expenseDetails.ExpenseDetailRequestDTO;
import com.nc.utility.SplitType;
import lombok.Data;

import java.util.List;

@Data
public class ExpenseRequest {
    private String expenseName;
    private String expenseType;
    private double expenseAmount;
    private List<Long> splitBetweenUserIds;
    private SplitType splitType;
    private Long groupId;
    private List<ExpenseDetailRequestDTO> expenseDetails;
}

package com.nc.expenseDetails;

import lombok.Data;

@Data
public class ExpenseDetailRequestDTO {
    private Long payerId;
    private Double amountPaid;
}
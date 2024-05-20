package com.nc.model;

import lombok.Data;

@Data
public class ExpenseDetailDTO {
    private Long id;
    private Long expenseId;
    private Long payerId;
    private Double amountPaid;
}

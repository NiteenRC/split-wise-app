package com.nc.model;

import lombok.Data;

@Data
public class ExpenseDetailDTO {
    private Integer id;
    private Integer expenseId;
    private Integer payerId;
    private Double amountPaid;
}

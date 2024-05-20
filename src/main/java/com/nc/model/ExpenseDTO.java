package com.nc.model;

import lombok.Data;

import java.util.List;

@Data
public class ExpenseDTO {
    private Long id;
    private String expenseName;
    private String expenseType;
    private Double expenseAmount;
    private Boolean status;
    private String splitType;
    private Long groupId;
    private List<ExpenseDetailDTO> expenseDetails;
}

package com.nc.model;

import lombok.Data;

import java.util.List;

@Data
public class ExpenseDTO {
    private Integer id;
    private String expenseName;
    private String expenseType;
    private Double expenseAmount;
    private Boolean status;
    private String splitType;
    private Integer groupId;
    private List<ExpenseDetailDTO> expenseDetails;
}

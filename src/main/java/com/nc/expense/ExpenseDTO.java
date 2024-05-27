package com.nc.expense;

import com.nc.expenseDetails.ExpenseDetailDTO;
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

    public static ExpenseDTO convertToDto(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setExpenseName(expense.getExpenseName());
        dto.setExpenseType(expense.getExpenseType());
        dto.setExpenseAmount(expense.getExpenseAmount());
        dto.setStatus(expense.getStatus());
        dto.setSplitType(expense.getSplitType().name());
        dto.setGroupId(expense.getGroup().getId());
        /*dto.setExpenseDetails(
                expense.getExpenseDetails().stream()
                        .map(ExpenseDetailDTO::fromExpenseDetails)
                        .toList()
        );*/
        return dto;
    }
}

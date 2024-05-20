package com.nc.model;

import com.nc.expense.Expense;
import com.nc.expenseDetails.ExpenseDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExpenseDtoConverter {

    public static ExpenseDTO convertToDto(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setExpenseName(expense.getExpenseName());
        dto.setExpenseType(expense.getExpenseType());
        dto.setExpenseAmount(expense.getExpenseAmount());
        dto.setStatus(expense.getStatus());
        dto.setSplitType(expense.getSplitType().name());
        dto.setGroupId(expense.getGroup().getId());
        List<ExpenseDetailDTO> detailDTOs = expense.getExpenseDetails().stream()
                .map(ExpenseDtoConverter::convertDetailToDto)
                .toList();
        dto.setExpenseDetails(detailDTOs);
        return dto;
    }

    public static ExpenseDetailDTO convertDetailToDto(ExpenseDetails details) {
        ExpenseDetailDTO dto = new ExpenseDetailDTO();
        dto.setId(details.getId());
        dto.setExpenseId(details.getExpense().getId());
        dto.setPayerId(details.getPayer().getId());
        dto.setAmountPaid(details.getAmountPaid());
        return dto;
    }
}

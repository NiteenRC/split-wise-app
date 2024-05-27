package com.nc.expenseDetails;

import lombok.Data;

@Data
public class ExpenseDetailDTO {
    private Long id;
    private Long expenseId;
    private Long payerId;
    private String payerName;
    private Double amountPaid;

    public static ExpenseDetailDTO fromExpenseDetails(ExpenseDetails expenseDetails) {
        ExpenseDetailDTO dto = new ExpenseDetailDTO();
        dto.setId(expenseDetails.getId());
        dto.setPayerId(expenseDetails.getPayee().getId());
        dto.setPayerName(expenseDetails.getPayee().getUsername());
        dto.setAmountPaid(expenseDetails.getAmountPaid());
        return dto;
    }
}

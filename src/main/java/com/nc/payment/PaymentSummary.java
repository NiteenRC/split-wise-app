package com.nc.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaymentSummary {
    private String expenseName;
    private List<PaymentDTO> users;
}

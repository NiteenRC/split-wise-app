package com.nc.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class BalanceSheetDTO {
    private Map<String, Double> owesAmount;
    private Map<String, Double> needToPay;
}
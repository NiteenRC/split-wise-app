package com.nc.balance;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BalanceSheet {
    private String userName;
    private List<String> owes;
    private List<String> owed;
    private double totalOwes;
    private double totalOwed;
}
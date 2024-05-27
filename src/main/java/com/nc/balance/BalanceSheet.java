package com.nc.balance;

import lombok.Data;

import java.util.List;

@Data
public class BalanceSheet {
    private String userName;
    private List<String> owes;
    private List<String> owed;
    private double totalOwes;
    private double totalOwed;
}
package com.nc.user;

import lombok.Data;

@Data
public class UserRequest {
    private double userAmountPaid;
    private Long payer;
}

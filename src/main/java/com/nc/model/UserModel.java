package com.nc.model;

import com.nc.user.User;
import lombok.Data;

@Data
public class UserModel {
    private double amountPaid;
    private User payer;
}

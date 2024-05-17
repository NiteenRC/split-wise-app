package com.nc.model;

import com.nc.group.Group;
import com.nc.payment.Payment;
import com.nc.user.User;
import lombok.Data;

import java.util.List;

@Data
public class ExpenseModel {
    private Integer id;
    private String expenseName;
    private String expenseType;
    private Double expenseAmount;
    private Double amountPaid;
    private Boolean status;
    private User user;
    private List<User> splitBetweenUsers;
    private Payment transaction;
    private Group group;
}

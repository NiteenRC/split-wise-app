package com.nc.transaction;

import com.nc.expense.Expense;
import com.nc.group.Group;
import com.nc.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private User user;
    private Double splitAmount;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    @ManyToOne
    @JoinColumn(name = "expense_id")
    private Expense expense;
}

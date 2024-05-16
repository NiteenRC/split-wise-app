package com.nc.expense;

import com.nc.group.Group;
import com.nc.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String expenseName;
    private String expenseType;
    private Double expenseAmount;
    private Double amountPaid;
    private Boolean status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany
    @JoinColumn(name = "user")
    private List<User> splitBetweenUsers;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    @OneToMany
    private List<Expense> expenses;
}
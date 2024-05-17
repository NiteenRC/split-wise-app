package com.nc.payment;

import com.nc.expense.Expense;
import com.nc.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double amount;
    @ManyToOne
    @JoinColumn(name = "payer_id")
    private User payerId;
    @ManyToOne
    @JoinColumn(name = "payee_id")
    private User payeeId;
    @ManyToOne
    @JoinColumn(name = "expense_id")
    private Expense expense;
}

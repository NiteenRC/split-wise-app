package com.nc.expense;

import com.nc.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ExpenseDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private User payerId;
    private Double amountPaid;
}

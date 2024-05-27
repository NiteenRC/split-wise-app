package com.nc.balance;

import com.nc.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user1;

    @ManyToOne
    private User user2;

    private double amount;
}

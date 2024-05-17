package com.nc.expense;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nc.expenseDetails.ExpenseDetails;
import com.nc.group.Group;
import com.nc.model.SplitType;
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
    private Boolean status;
    @Enumerated(EnumType.STRING)
    private SplitType splitType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "expense", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ExpenseDetails> expenseDetails;
}
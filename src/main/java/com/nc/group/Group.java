package com.nc.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nc.expense.Expense;
import com.nc.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "`group`")
@Data
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String groupName;
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "group_users",
            joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> users;
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Expense> expenses;
}

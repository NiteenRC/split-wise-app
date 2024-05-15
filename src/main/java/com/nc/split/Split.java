package com.nc.split;

import com.nc.group.Group;
import com.nc.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "split")
@Data
public class Split {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    @OneToOne
    //@JoinColumn(name = "user_id")
    private User user;
    private Double splitAmount;
}

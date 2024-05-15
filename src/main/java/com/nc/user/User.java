package com.nc.user;

import com.nc.group.Group;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "`user`")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String email;
    private String password;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
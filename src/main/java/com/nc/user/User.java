package com.nc.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nc.group.Group;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "`user`")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    @ManyToMany(cascade = CascadeType.MERGE, mappedBy = "users")
    @JsonIgnore
    private List<Group> userGroups;
}
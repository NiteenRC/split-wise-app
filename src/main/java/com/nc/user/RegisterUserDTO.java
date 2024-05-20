package com.nc.user;

import lombok.Data;

@Data
public class RegisterUserDTO {
    private String username;
    private String email;
    private String password;
    private String mobileNumber;
}

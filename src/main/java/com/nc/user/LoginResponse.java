package com.nc.user;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private long expiresIn;
}

package com.nc.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;

    // Static method to convert User to UserResponseDTO
    public static UserResponseDTO fromUser(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername());
    }
}

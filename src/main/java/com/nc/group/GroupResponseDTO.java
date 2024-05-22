package com.nc.group;

import com.nc.user.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class GroupResponseDTO {
    private Long id;
    private String groupName;
    private List<UserResponseDTO> users;

    // Static method to convert Group to GroupResponseDTO
    public static GroupResponseDTO fromGroup(Group group) {
        List<UserResponseDTO> userDTOs = group.getUsers().stream()
                .map(UserResponseDTO::fromUser)
                .collect(Collectors.toList());
        return new GroupResponseDTO(group.getId(), group.getGroupName(), userDTOs);
    }
}

package com.nc.group;

import lombok.Data;

import java.util.List;

@Data
public class GroupRequest {
    private String groupName;
    private List<Long> userIds;
}

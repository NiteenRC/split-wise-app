package com.nc.group;

import com.nc.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupById(@PathVariable Long groupId) {
        GroupResponseDTO group = groupService.getGroupById(groupId);
        return ResponseEntity.ok().body(group);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<GroupResponseDTO> group = groupService.getAllGroups();
        return ResponseEntity.ok().body(group);
    }

    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody GroupRequest groupRequest) {
        GroupResponseDTO createdGroup = groupService.saveOrUpdate(groupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }

    @PostMapping("/adduser/{groupId}")
    public ResponseEntity<?> addUser(@PathVariable("groupId") Long groupId, @RequestBody List<User> user) {
        return ResponseEntity.ok().body(groupService.addUser(groupId, user));
    }
}


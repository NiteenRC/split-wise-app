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
    public ResponseEntity<Group> getGroupById(@PathVariable Long groupId) {
        Group group = groupService.getGroupById(groupId);
        return ResponseEntity.ok().body(group);
    }

    @GetMapping("/groupName/{groupName}")
    public ResponseEntity<?> getGroupByGrouName(@PathVariable String groupName) {
        List<Group> group = groupService.getGroupByName(groupName);
        return ResponseEntity.ok().body(group);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Group> group = groupService.findAll();
        return ResponseEntity.ok().body(group);
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        Group createdGroup = groupService.saveOrUpdateGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }

    @PutMapping
    public ResponseEntity<Group> updateGroup(@RequestBody Group group) {
        Group updatedGroup = groupService.saveOrUpdateGroup(group);
        return ResponseEntity.ok().body(updatedGroup);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/adduser/{id}")
    public ResponseEntity<?> addUser(@PathVariable("id") Long GrpId, @RequestBody List<User> user) {
        return ResponseEntity.ok().body(groupService.addUser(GrpId, user));
    }
}


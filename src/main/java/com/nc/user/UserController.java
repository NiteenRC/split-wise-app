package com.nc.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        User createdUser = userService.saveOrUpdateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> createUsers(@RequestBody List<User> users) {
        List<User> createdUser = userService.saveOrUpdateUsers(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        User updatedUser = userService.saveOrUpdateUser(user);
        return ResponseEntity.ok().body(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}


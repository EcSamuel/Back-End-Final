package com.rulezero.playerconnector.controller;

import com.rulezero.playerconnector.controller.model.UsersData;
import com.rulezero.playerconnector.entity.Users;
import com.rulezero.playerconnector.exception.ResourceNotFoundException;
import com.rulezero.playerconnector.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UsersController {

    @Autowired
    private UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    public UsersData createUser(@RequestBody UsersData usersData) {
//        log.info("Requesting User Creation: {}", usersData);
//        return userService.saveUser(usersData);
//    }
    // TODO: working on ensuring whether it is better to pass in Entity or Data level- PT Source code explanation suggests data and Id instead of whole entities.
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        log.info("Requesting User Creation: {}", user);
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UsersData> partiallyUpdateUser(@PathVariable Long userId, @RequestBody UsersData usersData) {
        try {
            UsersData updatedUser = userService.patchUser(userId, usersData);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UsersData> getUserById(@PathVariable Long userId) {
        try {
            UsersData userData = userService.getUserById(userId);
            return ResponseEntity.ok(userData);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/search")
    public List<UsersData> searchUsers(@RequestParam String query) {
        log.info("Requesting User Search: {}", query);
        if (query == null || query.isEmpty()) {
            log.info("You must specify a query");
            return List.of(); // Return an empty list instead of null
        } else {
            return userService.searchUsersByName(query);
        }
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("Requesting User Deletion: {}", userId);
        try {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public List<UsersData> getAllUsers() {
        log.info("Requesting All Users");
        return userService.getAllUsers();
    }

    @PatchMapping("/{userId}/availability")
    public ResponseEntity<UsersData> updateUserAvailability(@PathVariable Long userId, @RequestParam Long availabilityId) {
        log.info("Updating availability for User {}: {}", userId, availabilityId);
        try {
            UsersData updatedUser = userService.updateUserAvailability(userId, availabilityId);
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUsers(@RequestBody List<Long> userIds) {
        log.info("Requesting Deletion of Users: {}", userIds);
        try {
            userService.deleteUsers(userIds);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
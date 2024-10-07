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

/**
 * REST Controller for managing user operations.
 * This controller handles all user-related HTTP requests including user creation, updates,
 * searches, and deletions. It also manages user availability associations and bulk operations.
 * The controller delegates business logic to the UserService while handling HTTP-specific concerns.
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UsersController {

    @Autowired
    private UserService userService;

    /**
     * Creates a new user in the system.
     * Note: Currently returns Users entity directly - under review for potential change
     * to return UsersData instead, following best practices for DTO usage.
     *
     * @param usersData The user data for creating the new user
     * @return ResponseEntity containing the created user entity and CREATED status
     * @throws IllegalArgumentException if the user data is invalid
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    public UsersData createUser(@RequestBody UsersData usersData) {
//        log.info("Requesting User Creation: {}", usersData);
//        return userService.saveUser(usersData);
//    }
    public ResponseEntity<Users> createUser(@RequestBody UsersData usersData) {
        log.info("Requesting User Creation: {}", usersData);
        return new ResponseEntity<>(userService.saveUser(usersData), HttpStatus.CREATED);
    }

    
    /**
     * Partially updates an existing user's information.
     * Only the provided fields in the request body will be updated.
     *
     * @param userId The ID of the user to update
     * @param usersData The partial user data containing only the fields to be updated
     * @return ResponseEntity containing the updated user data if successful,
     *         or appropriate error status if the update fails
     */
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

    /**
     * Retrieves a specific user by their ID.
     *
     * @param userId The ID of the user to retrieve
     * @return ResponseEntity containing the user data if found,
     *         or NOT_FOUND status if the user doesn't exist
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UsersData> getUserById(@PathVariable Long userId) {
        try {
            UsersData userData = userService.getUserById(userId);
            return ResponseEntity.ok(userData);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Searches for users based on a query string.
     * The search is performed on user names and returns matching results.
     *
     * @param query The search query string to match against user names
     * @return List of users matching the search query. Returns an empty list if the query is null or empty,
     *         or if no matches are found
     */
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

    /**
     * Deletes a specific user from the system.
     *
     * @param userId The ID of the user to delete
     * @return ResponseEntity with:
     *         - NO_CONTENT status if deletion is successful
     *         - NOT_FOUND status if the user doesn't exist
     *         - INTERNAL_SERVER_ERROR status if deletion fails for other reasons
     */
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

    /**
     * Retrieves all users in the system.
     *
     * @return List of all users in the system
     */
    @GetMapping
    public List<UsersData> getAllUsers() {
        log.info("Requesting All Users");
        return userService.getAllUsers();
    }

    /**
     * Updates a user's availability association.
     * Links a specific availability schedule to a user.
     *
     * @param userId The ID of the user to update
     * @param availabilityId The ID of the availability schedule to associate with the user
     * @return ResponseEntity containing the updated user data if successful,
     *         or appropriate error status if the update fails
     */
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

    /**
     * Performs bulk deletion of multiple users.
     *
     * @param userIds List of user IDs to delete
     * @return ResponseEntity with:
     *         - NO_CONTENT status if all deletions are successful
     *         - INTERNAL_SERVER_ERROR status if any deletion fails
     */
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

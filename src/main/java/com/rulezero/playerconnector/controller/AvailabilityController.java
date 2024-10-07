package com.rulezero.playerconnector.controller;

import com.rulezero.playerconnector.controller.model.AvailabilityData;
import com.rulezero.playerconnector.controller.model.UsersData;
import com.rulezero.playerconnector.entity.Availability;
import com.rulezero.playerconnector.entity.Games;
import com.rulezero.playerconnector.entity.Users;
import com.rulezero.playerconnector.service.AvailabilityService;
import com.rulezero.playerconnector.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for managing user availability operations.
 * This controller handles CRUD operations for user availability schedules,
 * allowing users to create, read, update, and delete their availability time slots.
 */
@RestController
@RequestMapping("/availabilities")
@Slf4j
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private UserService userService;

    /**
     * Creates a new availability entry for a specific user.
     *
     * @param availabilityData The availability data to be created
     * @param userId The ID of the user for whom the availability is being created
     * @return The created availability data
     * @throws IllegalArgumentException if the user is not found
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvailabilityData createAvailability(@RequestBody AvailabilityData availabilityData, @RequestParam Long userId) {
        log.info("Requesting Availability Creation: {} for user Id {}", availabilityData, userId);

        Users user = userService.getUserEntityById(userId);
        Availability availability = availabilityService.convertToEntity(availabilityData);

        user.addAvailability(availability);
        Users savedUser = userService.saveUser(convertToUsersData(user));

        List<Availability> availabilitiesList = new ArrayList<>(savedUser.getAvailabilities());
        Availability savedAvailability = availabilitiesList.get(availabilitiesList.size() - 1);
        return availabilityService.mapToData(savedAvailability);
    }

    /**
     * Converts a Users entity to UsersData DTO.
     *
     * @param user The Users entity to convert
     * @return The converted UsersData object
     */
    private UsersData convertToUsersData(Users user) {
        // TODO: Implement the conversion logic here
        UsersData usersData = new UsersData();
        usersData.setUserId(user.getUserId());
        usersData.setFirstName(user.getFirstName());
        usersData.setLastName(user.getLastName());
        usersData.setUserPhone(user.getUserPhone());
        usersData.setUserAddress(user.getUserAddress());
        usersData.setUserCity(user.getUserCity());
        usersData.setUserRegion(user.getUserRegion());
        usersData.setUserLoginName(user.getUserLoginName());
        usersData.setUserEmail(user.getUserEmail());
        usersData.setAvailabilityId(user.getUserAvailability() != null ? user.getUserAvailability().getAvailabilityId() : null);
        usersData.setGameIds(user.getGameUsers().stream().map(Games::getGameId).collect(Collectors.toSet()));
        return usersData;
    }

    /**
     * Retrieves all availability entries for a specific user.
     *
     * @param userId The ID of the user whose availabilities are being retrieved
     * @return A ResponseEntity containing a list of availability data
     * @throws IllegalArgumentException if the user is not found
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AvailabilityData>> getAvailabilitiesByUserId(@PathVariable Long userId) {
        Users user = userService.getUserEntityById(userId);
        List<AvailabilityData> availabilities = user.getAvailabilities().stream()
                .map(availabilityService::mapToData)
                .toList();
        return ResponseEntity.ok(availabilities);
    }

//    @GetMapping
//    public ResponseEntity<List<Availability>> getAllAvailabilities() {
//        return ResponseEntity.ok(availabilityService.getAllAvailabilities());
//    }

    @GetMapping("/{availabilityId}")
    public ResponseEntity<Availability> getAvailabilityById(@PathVariable Long id) {
        return ResponseEntity.ok(availabilityService.getAvailabilityById(id));
    }

    /**
     * Updates an existing availability entry.
     *
     * @param availabilityId The ID of the availability entry to update
     * @param availabilityData The updated availability data
     * @return A ResponseEntity containing the updated availability data
     * @throws IllegalArgumentException if the availability entry is not found
     */
    @PutMapping("/{availabilityId}")
    public ResponseEntity<AvailabilityData> updateAvailability(
            @PathVariable Long availabilityId,
            @RequestBody AvailabilityData availabilityData) {
        AvailabilityData updatedAvailability = availabilityService.updateAvailability(availabilityId, availabilityData);
        return ResponseEntity.ok(updatedAvailability);
    }
        // Broken, wrong, and possibly unused
//    @DeleteMapping("/user/{userId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteAvailabilityByUserId(@PathVariable Long userId) {
//
//        UsersData savedUser = userService.saveUser(convertToUsersData(userId));
//        savedUser.getAvailabilities().clear();
//        userService.saveUser(user);
//    }

    /**
     * Deletes a specific availability entry by its ID.
     *
     * @param availabilityId The ID of the availability entry to delete
     * @throws IllegalArgumentException if the availability entry is not found
     */
    @DeleteMapping("/{availabilityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvailabilityById(@PathVariable Long availabilityId) {
        availabilityService.deleteAvailabilityById(availabilityId);
    }
}

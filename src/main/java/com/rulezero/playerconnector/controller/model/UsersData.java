package com.rulezero.playerconnector.controller.model;

import lombok.Data;

import java.util.Set;

@Data
public class UsersData {

    private Long userId;
    private String firstName;
    private String lastName;
    private String userPhone;
    private String userAddress;
    private String userCity;
    private String userRegion;
    private String userLoginName;
    private String password;
    private String userEmail;
    private Long availabilityId; // Foreign key reference to Availability
    private Set<Long> gameIds;  // List of game IDs associated with the user

}

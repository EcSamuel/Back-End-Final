package com.rulezero.playerconnector.handler;

import com.rulezero.playerconnector.controller.model.UsersData;
import com.rulezero.playerconnector.entity.Users;
import com.rulezero.playerconnector.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMenuHandler {

    @Autowired
    private UserService usersService;

    private Scanner scanner = new Scanner(System.in);

    public void process() {
        List<String> userMenu = List.of(
                "1) Add a User",
                "2) List Users",
                "3) Select and Update a User",
                "4) Delete a User",
                "0) Back"
        );

        boolean back = false;
        while (!back) {
            int selection = getUserSelection(userMenu);
            switch (selection) {
                // TODO: Verify addUser
                case 1 -> addUser();
                // TODO: Verify listUsers
                case 2 -> listUsers();
                // TODO: Verify selectAndUpdateUser- currently StackOverflow when trying to update table relationships
                case 3 -> selectAndUpdateUser();
                // TODO: Verify deleteUser
                case 4 -> deleteUser();
                case 0 -> back = true;
                default -> System.out.println("Invalid selection");
            }
        }
    }

    private int getUserSelection(List<String> menu) {
        System.out.println("\nMake a selection:");
        menu.forEach(System.out::println);
        System.out.print("You selected: ");
        return Integer.parseInt(scanner.nextLine());
    }

    private void addUser() {

        System.out.println("Enter first name:");
        String firstName = scanner.nextLine();

        System.out.println("Enter last name:");
        String lastName = scanner.nextLine();

        System.out.println("Enter phone number:");
        String userPhone = scanner.nextLine();

        System.out.println("Enter address:");
        String userAddress = scanner.nextLine();

        System.out.println("Enter city:");
        String userCity = scanner.nextLine();

        System.out.println("Enter region:");
        String userRegion = scanner.nextLine();

        System.out.println("Enter login name:");
        String userLoginName = scanner.nextLine();

        System.out.println("Enter password:");
        String password = scanner.nextLine();

        System.out.println("Enter email:");
        String userEmail = scanner.nextLine();

//        System.out.println("Enter availability ID:");
//        Long availabilityId = Long.parseLong(scanner.nextLine());

        UsersData newUser = new UsersData();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setUserPhone(userPhone);
        newUser.setUserAddress(userAddress);
        newUser.setUserCity(userCity);
        newUser.setUserRegion(userRegion);
        newUser.setUserLoginName(userLoginName);
        newUser.setPassword(password);
        newUser.setUserEmail(userEmail);
        // TODO: Find out if this HooDoo Works
//        newUser.setAvailabilityId(null);
//        newUser.setGameIds(null);

        Users savedUser = usersService.saveUser(newUser);
        System.out.println("User added: " + savedUser.getUserId() + ": " + savedUser.getFirstName() + " " + savedUser.getLastName());
    }

    private void listUsers() {
        List<UsersData> users = usersService.getAllUsers();
        users.forEach(user -> System.out.println(user.getUserId() + ": " + user.getFirstName() + " " + user.getLastName()));
    }

    private void selectAndUpdateUser() {
        List<UsersData> users = usersService.getAllUsers();
        List<String> userNames = users.stream()
                .map(user -> user.getUserId() + ": " + user.getFirstName() + " " + user.getLastName())
                .collect(Collectors.toList());

        System.out.println("Select a user to update:");
        for (int i = 0; i < userNames.size(); i++) {
            System.out.println((i + 1) + ") " + userNames.get(i));
        }

        int selection = Integer.parseInt(scanner.nextLine()) - 1;
        if (selection >= 0 && selection < users.size()) {
            UsersData selectedUser = users.get(selection);
            updateUser(selectedUser);
        } else {
            System.out.println("Invalid selection");
        }
    }

    private void updateUser(UsersData existingUser) {
        System.out.println("Updating user: " + existingUser.getFirstName() + " " + existingUser.getLastName());

        System.out.println("Enter new first name (leave blank to keep current):");
        String firstName = scanner.nextLine();
        if (!firstName.isEmpty()) {
            existingUser.setFirstName(firstName);
        }

        System.out.println("Enter new last name (leave blank to keep current):");
        String lastName = scanner.nextLine();
        if (!lastName.isEmpty()) {
            existingUser.setLastName(lastName);
        }

        System.out.println("Enter new phone number (leave blank to keep current):");
        String userPhone = scanner.nextLine();
        if (!userPhone.isEmpty()) {
            existingUser.setUserPhone(userPhone);
        }

        System.out.println("Enter new address for user (leave blank to keep current):");
        String userAddress = scanner.nextLine();
        if (!userAddress.isEmpty()) {
            existingUser.setUserAddress(userAddress);
        }

        System.out.println("Enter new city for user (leave blank to keep current):");
        String userCity = scanner.nextLine();
        if (!userCity.isEmpty()) {
            existingUser.setUserCity(userCity);
        }

        System.out.println("Enter new region for user (leave blank to keep current):");
        String userRegion = scanner.nextLine();
        if (!userRegion.isEmpty()) {
            existingUser.setUserRegion(userRegion);
        }

        System.out.println("Enter new login name for user (leave blank to keep current):");
        String userLoginName = scanner.nextLine();
        if (!userLoginName.isEmpty()) {
            existingUser.setUserLoginName(userLoginName);
        }

        System.out.println("Enter new password for user (leave blank to keep current):");
        String password = scanner.nextLine();
        if (!password.isEmpty()) {
            existingUser.setPassword(password);
        }

        System.out.println("Enter new email for user (leave blank to keep current):");
        String userEmail = scanner.nextLine();
        if (!userEmail.isEmpty()) {
            existingUser.setUserEmail(userEmail);
        }

//        System.out.println("Update the user's games? (leave blank to keep current):");
//        String gameIdsInput = scanner.nextLine();
//        if (!gameIdsInput.isEmpty()) {
//            Set<Long> currentGameIds = existingUser.getGameIds();
//            if (currentGameIds == null) {
//                currentGameIds = new HashSet<>();
//            }
//            String[] newGameIds = gameIdsInput.trim().split(",");
//            for (String gameId : newGameIds) {
//                currentGameIds.add(Long.parseLong(gameId));
//            }
//            existingUser.setGameIds(currentGameIds);
//        }

        System.out.println("Update the user's games? (Enter game IDs separated by commas, or leave blank to keep current):");
        String gameIdsInput = scanner.nextLine();
        if (!gameIdsInput.isEmpty()) {
            Set<Long> updatedGameIds = new HashSet<>();
            for (String gameId : gameIdsInput.split(",")) {
                updatedGameIds.add(Long.parseLong(gameId.trim()));
            }
            existingUser.setGameIds(updatedGameIds);
        }

        System.out.println("Update user availability? (Enter availability ID, or leave blank to keep current):");
        String availabilityIdInput = scanner.nextLine();
        if (!availabilityIdInput.isEmpty()) {
            Long availabilityId = Long.parseLong(availabilityIdInput.trim());
            existingUser.setAvailabilityId(availabilityId);
        }

        UsersData updatedUser = usersService.patchUser(existingUser.getUserId(), existingUser);
        System.out.println("User updated: " + updatedUser.getUserId() + ": " + updatedUser.getFirstName() + " " + updatedUser.getLastName());
    }

    private void deleteUser() {
        System.out.println("Enter user ID to delete:");
        Long userId = Long.parseLong(scanner.nextLine());

        usersService.deleteUser(userId);
        System.out.println("User deleted.");
    }

    private UsersData convertToUsersData(Users user) {
        UsersData userData = new UsersData();
        userData.setUserId(user.getUserId());
        userData.setFirstName(user.getFirstName());
        userData.setLastName(user.getLastName());
        userData.setUserPhone(user.getUserPhone());
        userData.setUserAddress(user.getUserAddress());
        userData.setUserCity(user.getUserCity());
        userData.setUserRegion(user.getUserRegion());
        userData.setUserLoginName(user.getUserLoginName());
        userData.setUserEmail(user.getUserEmail());
        // Don't set the password here for security reasons
        userData.setAvailabilityId(user.getUserAvailability() != null ? user.getUserAvailability().getAvailabilityId() : null);
        userData.setGameIds(user.getGameUsers().stream().map(game -> game.getGameId()).collect(Collectors.toSet()));
        return userData;
    }
}

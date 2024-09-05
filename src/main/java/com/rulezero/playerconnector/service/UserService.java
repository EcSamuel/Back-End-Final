package com.rulezero.playerconnector.service;

import com.rulezero.playerconnector.controller.model.UsersData;
import com.rulezero.playerconnector.dao.GamesDao;
import com.rulezero.playerconnector.dao.UsersDao;
import com.rulezero.playerconnector.entity.Availability;
import com.rulezero.playerconnector.entity.Games;
import com.rulezero.playerconnector.entity.Users;
import com.rulezero.playerconnector.dao.AvailabilityDao;
import com.rulezero.playerconnector.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UsersDao usersDao;

    @Autowired
    private AvailabilityDao availabilityDao;

    @Autowired
    private GamesDao gamesDao;

    @Transactional
    public Users saveUser(UsersData usersData) {
        Users user = convertToEntity(usersData);
        return usersDao.save(user);
    }

    private Users convertToEntity(UsersData usersData) {
        Users user = new Users();
        user.setFirstName(usersData.getFirstName());
        user.setLastName(usersData.getLastName());
        user.setUserPhone(usersData.getUserPhone());
        user.setUserAddress(usersData.getUserAddress());
        user.setUserCity(usersData.getUserCity());
        user.setUserRegion(usersData.getUserRegion());
        user.setUserLoginName(usersData.getUserLoginName());
        user.setUserEmail(usersData.getUserEmail());
        user.setPassword(usersData.getPassword());

        if (usersData.getAvailabilityId() != null) {
            Availability availability = availabilityDao.findById(usersData.getAvailabilityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + usersData.getAvailabilityId()));
            user.setUserAvailabilities(availability);
        }

        if (usersData.getGameIds() != null) {
            Set<Games> games = usersData.getGameIds().stream()
                    .map(gameId -> gamesDao.findById(gameId)
                            .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId)))
                    .collect(Collectors.toSet());
            user.setGameUsers(games);
        }

        return user;
    }

    @Transactional
    public UsersData patchUser(Long userId, UsersData usersData) throws ResourceNotFoundException {
        Users existingUser = usersDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        updateUserFields(existingUser, usersData);

        Users updatedUser = usersDao.save(existingUser);
        return convertToUsersData(updatedUser);
    }
    // TODO: Revisit? Might not need this one at the moment
    public Users patchUsers(Long userId, UsersData usersData) throws ResourceNotFoundException {}

    private void updateUserFields(Users user, UsersData usersData) {
        if (usersData.getFirstName() != null) {
            user.setFirstName(usersData.getFirstName());
        }
        if (usersData.getLastName() != null) {
            user.setLastName(usersData.getLastName());
        }
        if (usersData.getUserPhone() != null) {
            user.setUserPhone(usersData.getUserPhone());
        }
        if (usersData.getUserAddress() != null) {
            user.setUserAddress(usersData.getUserAddress());
        }
        if (usersData.getUserCity() != null) {
            user.setUserCity(usersData.getUserCity());
        }
        if (usersData.getUserRegion() != null) {
            user.setUserRegion(usersData.getUserRegion());
        }
        if (usersData.getUserLoginName() != null) {
            user.setUserLoginName(usersData.getUserLoginName());
        }
        if (usersData.getUserEmail() != null) {
            user.setUserEmail(usersData.getUserEmail());
        }
        if (usersData.getAvailabilityId() != null) {
            Availability availability = availabilityDao.findById(usersData.getAvailabilityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + usersData.getAvailabilityId()));
            user.setUserAvailability(availability);
        }
        if (usersData.getGameIds() != null) {
            updateUserGames(user, usersData.getGameIds());
        }
    }

    public Users getUserEntityById(Long userId) {
        return usersDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Transactional
    public void updateUserGames(Users user, Set<Long> gameIds) {
        Set<Games> newGames = gameIds.stream()
                .map(gameId -> gamesDao.findById(gameId)
                        .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId)))
                .collect(Collectors.toSet());

        // Remove games not in the new set
        user.getGameUsers().removeIf(game -> !newGames.contains(game));

        // Add new games
        newGames.forEach(game -> {
            if (!user.getGameUsers().contains(game)) {
                user.getGameUsers().add(game);
                game.getGameUsers().add(user);
            }
        });
    }
    @Transactional
    public void deleteUser(Long userId) throws ResourceNotFoundException {
        Users user = usersDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Remove the user from all games and stores
        user.getGameUsers().forEach(game -> game.getGameUsers().remove(user));

        usersDao.delete(user);
    }

    public UsersData getUserById(Long userId) {
        Users user = usersDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return convertToUsersData(user);
    }
    // TODO: Check out Alternative Method where it is called by User instead of UsersData
    public Users getUsersById(Long userId) {
        return usersDao.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public List<UsersData> searchUsersByName(String query) {
        List<Users> users = usersDao.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query);
        return users.stream()
                .map(this::convertToUsersData)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<UsersData> getAllUsers() {
        List<Users> users = usersDao.findAll();
        return users.stream()
                .map(this::convertToUsersData)
                .collect(Collectors.toList());
    }

    public List<Users> getAllTheUsers() {
        return usersDao.findAll();
    }

    private UsersData convertToUsersData(Users user) {
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

    @Transactional
    public void deleteUsers(List<Long> userIds) {
        List<Users> users = usersDao.findAllById(userIds);
        users.forEach(user -> {
            user.getGameUsers().forEach(game -> game.getGameUsers().remove(user));
            usersDao.delete(user);
        });
    }

    @Transactional
    public UsersData updateUserAvailability(Long userId, Long availabilityId) throws ResourceNotFoundException {
        Users user = usersDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Availability availability = availabilityDao.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + availabilityId));

        user.setUserAvailability(availability);
        Users updatedUser = usersDao.save(user);
        return convertToUsersData(updatedUser);
    }

    public void addUserAvailability(Long userId, Long availabilityId) {
        Users user = getUserEntityById(userId);
        Availability availability = availabilityService.getAvailabilityById(availabilityId);
        user.addAvailability(availability);
        usersDao.save(user);
    }

    public void removeUserAvailability(Long userId, Long availabilityId) {
        Users user = getUserEntityById(userId);
        Availability availability = availabilityService.getAvailabilityById(availabilityId);
        user.removeAvailability(availability);
        usersDao.save(user);
    }
}
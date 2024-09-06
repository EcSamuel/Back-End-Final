package com.rulezero.playerconnector.service;

import com.rulezero.playerconnector.controller.model.AvailabilityData;
import com.rulezero.playerconnector.dao.UsersDao;
import com.rulezero.playerconnector.entity.Availability;
import com.rulezero.playerconnector.entity.Users;
import com.rulezero.playerconnector.exception.ResourceNotFoundException;
import com.rulezero.playerconnector.dao.AvailabilityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityDao availabilityDao;

    @Autowired
    private UsersDao usersDao;

//    @Transactional
//    public Availability saveAvailability(Availability availability, Users users) {
//        if (users == null || availability == null) {
//            throw new IllegalArgumentException("User and Availability must not be null");
//        }
//
//        if (availability.getAvailabilityId() != null) {
//            Optional<Availability> existingAvailability = availabilityDao.findById(availability.getAvailabilityId());
//
//            if (existingAvailability.isPresent()) {
//                Availability availToUpdate = existingAvailability.get();
//                availToUpdate.setStartTime(availability.getStartTime());
//                availToUpdate.setEndTime(availability.getEndTime());
//                availToUpdate.setDayOfWeek(availability.getDayOfWeek());
//                return availabilityDao.save(availToUpdate);
//            }
//        }
//        // If the availability ID is null or it doesn't exist in the database
//        availability.setUser(users);
//        return availabilityDao.save(availability);
//    }

    public Availability saveAvailability(Long userId, Availability availability) {
        Users user = usersDao.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        user.getAvailabilities().add(availability);
        usersDao.save(user);
        return availability;
    }

    public Availability getAvailabilityById(Long availabilityId) {
        return availabilityDao.findById(availabilityId).orElseThrow(() -> new ResourceNotFoundException("Availability not found"));
    }

//    public AvailabilityData getAvailabilityById(Long availabilityId) {
//        Availability availability = availabilityDao.findById(availabilityId)
//                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + availabilityId));
//        return mapToData(availability);
//    }

    public List<AvailabilityData> getAllAvailabilities() {
        List<Availability> availabilities = availabilityDao.findAll();
        return availabilities.stream()
                .map(this::convertToAvailabilitiesData)
                .collect(Collectors.toList());
    }



    private AvailabilityData convertToAvailabilitiesData(Availability availability) {
        AvailabilityData availabilityData = new AvailabilityData();
        availabilityData.setAvailabilityId(availability.getAvailabilityId());
        availabilityData.setDayOfWeek(availability.getDayOfWeek());
        availabilityData.setStartTime(availability.getStartTime());
        availabilityData.setEndTime(availability.getEndTime());
        return availabilityData;
    }

    public Availability updateAvailability(Long availabilityId, Availability availability) {
        Availability availabilityToUpdate = getAvailabilityById(availabilityId);
        // add in fields with getters and setters correctly.
        return availabilityDao.save(availabilityToUpdate);
    }



//    public List<AvailabilityData> getAllAvailabilities() {
//        List<Availability> availabilities = availabilityDao.findAll();
//        return availabilities.stream()
//                .map(this::mapToData)
//                .collect(Collectors.toList());
//    }
    // TODO: Rewrite on the user side to remove over here since unidirectional. Might need to reconfigure on the MenuHandler as well.
//    public List<AvailabilityData> getAvailabilityByUserId(Long userId) {
//        List<Availability> availabilities = availabilityDao.findByUser_UserId(userId);
//        return availabilities.stream().map(this::mapToData).collect(Collectors.toList());
//    }

    public AvailabilityData mapToData(Availability availability) {
        AvailabilityData data = new AvailabilityData();
        data.setAvailabilityId(availability.getAvailabilityId());
        data.setDayOfWeek(availability.getDayOfWeek());
        data.setStartTime(availability.getStartTime());
        data.setEndTime(availability.getEndTime());
//        data.setUserId(availability.getUser().getUserId());
        return data;
    }

    public List<AvailabilityData> getAllAvailability() {
        List<Availability> availabilities = availabilityDao.findAll();
        return availabilities.stream()
                .map(this::mapToData)
                .collect(Collectors.toList());
    }

    @Transactional
    public AvailabilityData updateAvailability(Long availabilityId, AvailabilityData availabilityData) throws ResourceNotFoundException {
        Availability existingAvailability = availabilityDao.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + availabilityId));

        existingAvailability.setDayOfWeek(availabilityData.getDayOfWeek());
        existingAvailability.setStartTime(availabilityData.getStartTime());
        existingAvailability.setEndTime(availabilityData.getEndTime());

        Availability updatedAvailability = availabilityDao.save(existingAvailability);
        return mapToData(updatedAvailability);
    }

    // TODO: Rewrite on User side or delete altogether
//    @Transactional
//    public void deleteAvailabilityByUserId(Long userId) {
//        List<Availability> availabilities = availabilityDao.findByUser_UserId(userId);
//        availabilityDao.deleteAll(availabilities);
//    }

    public void deleteAvailability(Long availabilityId) {
        Availability availability = getAvailabilityById(availabilityId);
        availabilityDao.delete(availability);
    }

    @Transactional
    public void deleteAvailabilityById(Long availabilityId) {
        Availability availability = availabilityDao.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + availabilityId));
        availabilityDao.delete(availability);
    }

    public Availability convertToEntity(AvailabilityData availabilityData) {
        Availability availability = new Availability();
        availability.setAvailabilityId(availabilityData.getAvailabilityId());
        availability.setDayOfWeek(availabilityData.getDayOfWeek());
        availability.setStartTime(availabilityData.getStartTime());
        availability.setEndTime(availabilityData.getEndTime());
//        if (availabilityData.getUserId() != null) {
//            Users user = usersDao.findById(availabilityData.getUserId())
//                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + availabilityData.getUserId()));
//            availability.setUser(user);
//        }
        return availability;
    }

    // unsure if I'll ever need this one
//    public List<AvailabilityData> getAvailabilityByDateRange(LocalDate startDate, LocalDate endDate) {
//        List<Availability> availabilities = availabilityDao.findByDateRange(startDate, endDate);
//        return availabilities.stream()
//                .map(this::mapToData)
//                .collect(Collectors.toList());
//    }


}

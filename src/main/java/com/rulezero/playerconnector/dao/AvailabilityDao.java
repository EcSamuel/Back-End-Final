package com.rulezero.playerconnector.dao;

import com.rulezero.playerconnector.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityDao extends JpaRepository<Availability, Long> {
}
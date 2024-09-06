package com.rulezero.playerconnector.dao;

import com.rulezero.playerconnector.entity.Games;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GamesDao extends JpaRepository<Games, Long> {
    List<Games> findByNameContainingIgnoreCase(String name);
}

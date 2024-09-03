package com.rulezero.playerconnector.controller.model;

import com.rulezero.playerconnector.entity.Users;
import lombok.Data;

import java.util.Set;

@Data
public class GamesData {
    private Long gameId;
    private String gameName;
    private String gameDescription;
    private Set<Users> gameUsers;
    private Integer minPlayers;
    private Integer maxPlayers;
}
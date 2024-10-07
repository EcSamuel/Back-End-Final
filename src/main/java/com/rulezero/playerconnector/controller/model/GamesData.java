package com.rulezero.playerconnector.controller.model;

import com.rulezero.playerconnector.entity.Users;
import lombok.Data;

import java.util.Set;

//Column level information for the MySQL Database
@Data
public class GamesData {
    private Long gameId;
    private String gameName;
    private String gameDescription;
    private Set<Users> gameUsers;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Set<Long> userIds;
}

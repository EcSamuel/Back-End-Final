package com.rulezero.playerconnector.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Games {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column
    private String name;

    @Column
    private Integer minPlayers;

    @Column
    private Integer maxPlayers;

    @Column
    private String description;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "gameUsers", fetch = FetchType.EAGER)
    private Set<Users> gameUsers = new HashSet<>();

    // Helper methods for managing players
    public void addGameUser(Users user) {
        this.gameUsers.add(user);
        user.getGameUsers().add(this);
    }

    public void removeGameUser(Users user) {
        this.gameUsers.remove(user);
        user.getGameUsers().remove(this);
    }

}


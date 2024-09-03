package com.rulezero.playerconnector.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@ToString(exclude={"players","stores"})
@EqualsAndHashCode(exclude={"players","stores"})
public class Users {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // Maps to user_id in your SQL

    @Column(name = "first_name", nullable = false, length = 64)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 128)
    private String lastName;

    @Column()
    private String userPhone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String userAddress;

    @Column(nullable = false, length = 128)
    private String userCity;

    @Column(nullable = false, length = 128)
    private String userRegion;

    @Column(nullable = false, unique = true)
    private String userLoginName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String userEmail;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Availability> availabilities = new HashSet<>();

    public Availability getUserAvailability() {
        return availabilities.stream()
                .findFirst()
                .orElse(null);
    }

    public void setUserAvailability(Availability availability) {
        if (availability != null) {
            availability.setUser(this);
            this.availabilities.add(availability);
        } else {
            this.availabilities.removeIf(a -> a.getUser().equals(this));
        }
    }

    // TODO: These relationships need verification so that their relationships work
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "game_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private Set<Games> gameUsers = new HashSet<>();
}
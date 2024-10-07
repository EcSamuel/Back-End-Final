package com.rulezero.playerconnector.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Getter
@Setter
public class Users {

    //Primary Key
    @Id
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

    // One to Many Relationship for User and Availability. Availability needs to be deleted upon user removal to prevent orphans since the assignment required a one to many relationship (even though a real project structure would benefit from this being many to many)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private Set<Availability> availabilities = new HashSet<>();

    // helper methods for user- availability relationship
    public void addAvailability(Availability availability) {
        availabilities.add(availability);
    }

    public void removeAvailability(Availability availability) {
        availabilities.remove(availability);
    }

    public void setUserAvailabilities(Set<Availability> availabilities) {
        this.availabilities = availabilities;
    }

    // This method can be used when you need to get a single availability
    public Availability getUserAvailability() {
        return availabilities.isEmpty() ? null : availabilities.iterator().next();
    }

    //Many to Many relationship between users and games (from the owning side)
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "game_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private Set<Games> gameUsers = new HashSet<>();

    // Code I am no longer running but wanted to reexamine later
//    public void setUserAvailability(Availability availability) {
//        if (availability != null) {
//            availability.setUser(this);
//            this.availabilities.add(availability);
//        } else {
//            this.availabilities.removeIf(a -> a.getUser().equals(this));
//        }
//    }
}

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<Availability> availabilities = new HashSet<>();
//
//    public Availability getUserAvailability() {
//        return availabilities.stream()
//                .findFirst()
//                .orElse(null);
//    }
//


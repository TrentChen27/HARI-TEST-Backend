package com.pitt.hari_exercise_tracker.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "health_coach")
public class HealthCoach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String firstName;

    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String specialization;

    @Lob // Use @Lob for potentially long text fields
    private String bio;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "coach_clients",
            joinColumns = @JoinColumn(name = "coach_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )

    @JsonIgnore // Avoid infinite loops in API responses
    private Set<AppUser> clients;

}

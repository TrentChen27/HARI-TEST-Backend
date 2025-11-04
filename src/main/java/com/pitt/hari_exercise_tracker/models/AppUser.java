package com.pitt.hari_exercise_tracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String deviceUuid;

    private String firstName;

    private String lastName;

    private Instant createdDate;

    private Instant lastLoginDate;

    @Embedded
    private BioInfo bioInfo;

    @Lob
    private String healthGoals;
    @Lob
    private String medicalConditions;

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("appUser")
    private List<ExerciseRecord> exerciseRecords;

    @ManyToMany(mappedBy = "clients", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<HealthCoach> coaches;

}

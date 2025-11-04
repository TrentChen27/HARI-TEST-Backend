package com.pitt.hari_exercise_tracker.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exercise_records")
public class ExerciseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String exerciseType;

    private String exerciseDuration;

    private String exerciseLocation;

    @Lob
    private String exerciseNotes;

    private String exerciseIntensity;

    private Double caloriesBurned;

    @Column(nullable = false)
    private Instant dateTime;

    private Integer sets;
    private Integer reps;
    private Double weightKg;


    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("exerciseRecords")
    private AppUser appUser;
}


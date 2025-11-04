package com.pitt.hari_exercise_tracker.dto;

import com.pitt.hari_exercise_tracker.models.AppUser;
import lombok.Data;
import java.time.Instant;

/**
 * Data Transfer Object for *sending* ExerciseRecord data back to the client.
 * This is a "safe" object that can be customized to only show what the client needs.
 */
@Data
public class ExerciseRecordResponseDTO {

    private Long id;
    private String exerciseType;
    private String exerciseDuration;
    private String exerciseLocation;
    private String exerciseNotes;
    private String exerciseIntensity;
    private String caloriesBurned;
    private Instant dateTime;
    private Integer sets;
    private Integer reps;
    private Double weightKg;
    private String imageUrl;

    private String username;
}

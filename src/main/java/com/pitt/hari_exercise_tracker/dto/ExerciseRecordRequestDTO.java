package com.pitt.hari_exercise_tracker.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for *receiving* data to create/update an ExerciseRecord.
 * This object is clean and doesn't contain any database-specific info.
 */
@Data
public class ExerciseRecordRequestDTO {

    private String exerciseType;
    private String exerciseDuration;
    private String exerciseLocation;
    private String exerciseNotes;
    private String exerciseIntensity;
    private String caloriesBurned;
    private LocalDateTime dateTime;
    private Integer sets;
    private Integer reps;
    private Double weightKg;
    private String imageUrl;
}

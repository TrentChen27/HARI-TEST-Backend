package com.pitt.hari_exercise_tracker.models;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * An @Embeddable class.
 * Its fields will be stored as columns directly in the 'app_users' table.
 * This keeps our code organized without adding a complex table join.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable // The magic annotation!
public class BioInfo {

    private Instant birthDate;

    // --- Core physical metrics from your list ---
    private Double heightCm;
    private Double weightKg;

    // --- Activity Level from your list ---
    // We can use an Enum for this to be clean
    @Enumerated(EnumType.STRING)
    private FitnessLevel fitnessLevel;

    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;
}

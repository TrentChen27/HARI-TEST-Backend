package com.pitt.hari_exercise_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * A DTO to represent a single user's row on the coach's dashboard.
 * This combines data from AppUser and ExerciseRecord.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardUserDTO {
    private Long userId;
    private String username;
    private boolean alert; // true if no report in > 2 days
    private Instant lastTimeOfReport;
}

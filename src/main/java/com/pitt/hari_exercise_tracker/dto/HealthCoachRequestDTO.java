package com.pitt.hari_exercise_tracker.dto;

import lombok.Data;

// DTO for *receiving* data to register a new HealthCoach
@Data
public class HealthCoachRequestDTO {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String specialization;
    private String bio;
}

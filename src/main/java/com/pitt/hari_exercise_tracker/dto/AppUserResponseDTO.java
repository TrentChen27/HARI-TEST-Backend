package com.pitt.hari_exercise_tracker.dto;

import com.pitt.hari_exercise_tracker.models.BioInfo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppUserResponseDTO {

    private long id;
    private String username;
    private String email;
    private String deviceUuid;
    private String firstName;
    private String lastName;
    private LocalDateTime createdDate;
    private LocalDateTime lastLoginDate;
    private BioInfo bioInfo;
    private String healthGoals;
    private String medicalConditions;
    private String token;
}
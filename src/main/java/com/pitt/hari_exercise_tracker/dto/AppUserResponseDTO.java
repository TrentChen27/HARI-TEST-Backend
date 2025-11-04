package com.pitt.hari_exercise_tracker.dto;

import com.pitt.hari_exercise_tracker.models.BioInfo;
import lombok.Data;
import java.time.Instant;

@Data
public class AppUserResponseDTO {
    private long id;
    private String username;
    private String email;
    private String deviceUuid;
    private String firstName;
    private String lastName;
    private Instant createdDate;
    private Instant lastLoginDate;
    private BioInfo bioInfo;
    private String healthGoals;
    private String medicalConditions;
}

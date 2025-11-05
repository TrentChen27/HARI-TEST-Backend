package com.pitt.hari_exercise_tracker.dto;

import lombok.Data;
import java.util.Set;

@Data
public class HealthCoachResponseDTO {
    private long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String specialization;
    private String bio;
    private Set<AppUserResponseDTO> clients;
    private String token;
}
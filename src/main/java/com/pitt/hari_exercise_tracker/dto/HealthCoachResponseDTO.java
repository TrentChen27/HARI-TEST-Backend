package com.pitt.hari_exercise_tracker.dto;

import lombok.Data;
import java.util.Set;

// DTO for *sending* HealthCoach data back to the client
@Data
public class HealthCoachResponseDTO {
    private long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String specialization;
    private String bio;

    // We can include a "safe" list of their clients
    private Set<AppUserResponseDTO> clients;
}

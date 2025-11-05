package com.pitt.hari_exercise_tracker.dto;

import lombok.Data;

/**
 * DTO for the automatic "remember me" login.
 */
@Data
public class UuidLoginRequestDTO {
    private String deviceUuid;
}

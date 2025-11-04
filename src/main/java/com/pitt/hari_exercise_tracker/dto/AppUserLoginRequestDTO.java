package com.pitt.hari_exercise_tracker.dto;

import lombok.Data;

/**
 * DTO for the initial username/password login from the mobile app.
 * It also includes the deviceUuid to register it for "remember me".
 */
@Data
public class AppUserLoginRequestDTO {
    private String username;
    private String password;
    private String deviceUuid;
}
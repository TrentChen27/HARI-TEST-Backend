package com.pitt.hari_exercise_tracker.dto;

import lombok.Data;

@Data
public class AppUserLoginRequestDTO {
    private String loginIdentifier; // Was 'username'
    private String password;
    private String deviceUuid;
}
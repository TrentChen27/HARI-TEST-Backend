package com.pitt.hari_exercise_tracker.dto;

import lombok.Data;

@Data
public class AppUserRequestDTO {
    private String username;
    private String password;
    private String email;
    private String deviceUuid;
}

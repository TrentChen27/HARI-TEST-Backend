package com.pitt.hari_exercise_tracker.controller;

import com.pitt.hari_exercise_tracker.dto.AppUserLoginRequestDTO;
import com.pitt.hari_exercise_tracker.dto.AppUserRequestDTO;
import com.pitt.hari_exercise_tracker.dto.AppUserResponseDTO;
import com.pitt.hari_exercise_tracker.dto.UuidLoginRequestDTO;
import com.pitt.hari_exercise_tracker.service.AppUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    /**
     * POST /api/users/register
     * Registers a new AppUser using DTOs.
     */
    @PostMapping("/register")
    public ResponseEntity<AppUserResponseDTO> registerUser(@RequestBody AppUserRequestDTO requestDTO) {
        try {
            AppUserResponseDTO newUser = appUserService.registerUser(requestDTO);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // This catches the "Username or email already in use" error
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST /api/users/login
     * Authenticates with username/password and registers a device.
     */
    @PostMapping("/login")
    public ResponseEntity<AppUserResponseDTO> loginWithPassword(@RequestBody AppUserLoginRequestDTO loginRequest) {
        try {
            AppUserResponseDTO user = appUserService.loginWithPassword(loginRequest);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * POST /api/users/login-uuid
     * Authenticates with a deviceUuid for "remember me".
     */
    @PostMapping("/login-uuid")
    public ResponseEntity<AppUserResponseDTO> loginWithUuid(@RequestBody UuidLoginRequestDTO loginRequest) {
        try {
            AppUserResponseDTO user = appUserService.loginWithUuid(loginRequest);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
}


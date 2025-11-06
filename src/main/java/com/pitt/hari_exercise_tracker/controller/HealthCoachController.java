package com.pitt.hari_exercise_tracker.controller;

import com.pitt.hari_exercise_tracker.dto.HealthCoachRequestDTO;
import com.pitt.hari_exercise_tracker.dto.HealthCoachResponseDTO;
import com.pitt.hari_exercise_tracker.dto.LoginRequest;
import com.pitt.hari_exercise_tracker.service.HealthCoachService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coaches")
public class HealthCoachController {

    private final HealthCoachService healthCoachService;

    public HealthCoachController(HealthCoachService healthCoachService) {
        this.healthCoachService = healthCoachService;
    }

    /**
     * POST /api/coaches/register
     * Registers a new HealthCoach using DTOs.
     */
    @PostMapping("/register")
    public ResponseEntity<HealthCoachResponseDTO> registerCoach(@RequestBody HealthCoachRequestDTO requestDTO) {
        try {
            HealthCoachResponseDTO newCoach = healthCoachService.registerCoach(requestDTO);
            return new ResponseEntity<>(newCoach, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST /api/coaches/login
     * Authenticates a HealthCoach and returns a safe DTO.
     */
    @PostMapping("/login")
    public ResponseEntity<HealthCoachResponseDTO> loginCoach(@RequestBody LoginRequest loginRequest) {
        try {
            HealthCoachResponseDTO coach = healthCoachService.loginCoach(loginRequest);
            return new ResponseEntity<>(coach, HttpStatus.OK);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
}


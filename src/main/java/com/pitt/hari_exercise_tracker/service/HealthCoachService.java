package com.pitt.hari_exercise_tracker.service;

import com.pitt.hari_exercise_tracker.dto.HealthCoachRequestDTO;
import com.pitt.hari_exercise_tracker.dto.HealthCoachResponseDTO;
import com.pitt.hari_exercise_tracker.dto.LoginRequest;
import com.pitt.hari_exercise_tracker.mapper.HealthCoachMapper;
import com.pitt.hari_exercise_tracker.models.HealthCoach;
import com.pitt.hari_exercise_tracker.repository.HealthCoachRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class HealthCoachService {

    private final HealthCoachRepository healthCoachRepository;
    private final PasswordEncoder passwordEncoder;
    private final HealthCoachMapper healthCoachMapper;

    public HealthCoachService(HealthCoachRepository healthCoachRepository,
                              PasswordEncoder passwordEncoder,
                              HealthCoachMapper healthCoachMapper) {
        this.healthCoachRepository = healthCoachRepository;
        this.passwordEncoder = passwordEncoder;
        this.healthCoachMapper = healthCoachMapper;
    }

    /**
     * Registers a new HealthCoach using DTOs.
     */
    public HealthCoachResponseDTO registerCoach(HealthCoachRequestDTO requestDTO) {
        // Check if username or email is already taken
        if (healthCoachRepository.findByUsername(requestDTO.getUsername()).isPresent() ||
                healthCoachRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Username or email already in use");
        }

        // Convert DTO to entity
        HealthCoach newCoach = healthCoachMapper.toEntity(requestDTO);

        // Hash and set password
        newCoach.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        // Save and convert to Response DTO
        HealthCoach savedCoach = healthCoachRepository.save(newCoach);
        return healthCoachMapper.toResponseDTO(savedCoach);
    }

    /**
     * Authenticates a HealthCoach and returns a safe DTO.
     */
    public HealthCoachResponseDTO loginCoach(LoginRequest loginRequest) {
        // Find user by username
        HealthCoach coach = healthCoachRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Invalid username or password"));

        // Check password
        if (passwordEncoder.matches(loginRequest.getPassword(), coach.getPassword())) {
            // Passwords match, return the safe DTO
            return healthCoachMapper.toResponseDTO(coach);
        } else {
            // Passwords don't match
            throw new IllegalArgumentException("Invalid username or password");
        }
    }
}


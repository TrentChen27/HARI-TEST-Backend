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
    private final JwtService jwtService; // <-- 1. INJECT JwtService

    public HealthCoachService(HealthCoachRepository healthCoachRepository,
                              PasswordEncoder passwordEncoder,
                              HealthCoachMapper healthCoachMapper,
                              JwtService jwtService) {
        this.healthCoachRepository = healthCoachRepository;
        this.passwordEncoder = passwordEncoder;
        this.healthCoachMapper = healthCoachMapper;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new HealthCoach.
     */
    public HealthCoachResponseDTO registerCoach(HealthCoachRequestDTO requestDTO) {
        if (healthCoachRepository.findByUsername(requestDTO.getUsername()).isPresent() ||
                healthCoachRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Username or email already in use");
        }
        HealthCoach newCoach = healthCoachMapper.toEntity(requestDTO);
        newCoach.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        HealthCoach savedCoach = healthCoachRepository.save(newCoach);
        String token = jwtService.generateToken(savedCoach);
        HealthCoachResponseDTO responseDTO = healthCoachMapper.toResponseDTO(savedCoach);
        responseDTO.setToken(token);

        return responseDTO;
    }

    /**
     * Authenticates a HealthCoach and returns a DTO with a token.
     */
    public HealthCoachResponseDTO loginCoach(LoginRequest loginRequest) {
        // --- 1. FIND COACH BY USERNAME OR EMAIL ---
        String identifier = loginRequest.getLoginIdentifier();

        // Find coach by username OR email
        HealthCoach coach = healthCoachRepository.findByUsername(identifier) //
                .or(() -> healthCoachRepository.findByEmail(identifier)) //
                .orElseThrow(() -> new EntityNotFoundException("Invalid credentials"));
        // --- END OF NEW LOGIC ---

        // --- 2. CHECK PASSWORD ---
        if (passwordEncoder.matches(loginRequest.getPassword(), coach.getPassword())) {
            // Passwords match.
            String token = jwtService.generateToken(coach);
            HealthCoachResponseDTO responseDTO = healthCoachMapper.toResponseDTO(coach);
            responseDTO.setToken(token);

            return responseDTO;
        } else {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }
}
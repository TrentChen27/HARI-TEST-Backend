package com.pitt.hari_exercise_tracker.service;

import com.pitt.hari_exercise_tracker.dto.AppUserLoginRequestDTO;
import com.pitt.hari_exercise_tracker.dto.AppUserRequestDTO;
import com.pitt.hari_exercise_tracker.dto.AppUserResponseDTO;
import com.pitt.hari_exercise_tracker.dto.UuidLoginRequestDTO;
import com.pitt.hari_exercise_tracker.mapper.AppUserMapper;
import com.pitt.hari_exercise_tracker.models.AppUser;
import com.pitt.hari_exercise_tracker.repository.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppUserMapper appUserMapper; // Inject the mapper

    public AppUserService(AppUserRepository appUserRepository,
                          PasswordEncoder passwordEncoder,
                          AppUserMapper appUserMapper) { // Add mapper
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.appUserMapper = appUserMapper; // Init mapper
    }

    /**
     * Registers a new AppUser from a DTO.
     */
    public AppUserResponseDTO registerUser(AppUserRequestDTO requestDTO) {
        // Check if user already exists
        if (appUserRepository.findByUsername(requestDTO.getUsername()).isPresent() ||
                appUserRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Username or email already in use");
        }

        // Create new AppUser entity
        AppUser newUser = appUserMapper.toEntity(requestDTO); // Use mapper
        newUser.setPassword(passwordEncoder.encode(requestDTO.getPassword())); // Hash password
        newUser.setCreatedDate(Instant.now());

        AppUser savedUser = appUserRepository.save(newUser);
        return appUserMapper.toResponseDTO(savedUser); // Use mapper
    }

    /**
     * Authenticates a user with username/password and links their deviceUuid.
     */
    public AppUserResponseDTO loginWithPassword(AppUserLoginRequestDTO loginRequest) {
        // Find user by username
        AppUser user = appUserRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Invalid username or password"));

        // Check password
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // Passwords match.
            // Update their deviceUuid and last login time
            user.setDeviceUuid(loginRequest.getDeviceUuid());
            user.setLastLoginDate(Instant.now());
            AppUser savedUser = appUserRepository.save(user);

            // Return the safe DTO
            return appUserMapper.toResponseDTO(savedUser);
        } else {
            // Passwords don't match
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    /**
     * Authenticates a user automatically using their deviceUuid.
     */
    public AppUserResponseDTO loginWithUuid(UuidLoginRequestDTO loginRequest) {
        // Find user by deviceUuid
        AppUser user = appUserRepository.findByDeviceUuid(loginRequest.getDeviceUuid())
                .orElseThrow(() -> new EntityNotFoundException("Device not recognized. Please log in."));

        // Device found. Update last login time and return.
        user.setLastLoginDate(Instant.now());
        AppUser savedUser = appUserRepository.save(user);
        return appUserMapper.toResponseDTO(savedUser);
    }
}


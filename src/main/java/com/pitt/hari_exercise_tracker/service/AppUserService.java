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

import java.time.LocalDateTime;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppUserMapper appUserMapper;
    private final JwtService jwtService; // <-- 1. INJECT JwtService

    public AppUserService(AppUserRepository appUserRepository,
                          PasswordEncoder passwordEncoder,
                          AppUserMapper appUserMapper,
                          JwtService jwtService) { // <-- 2. ADD to constructor
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.appUserMapper = appUserMapper;
        this.jwtService = jwtService; // <-- 3. INITIALIZE it
    }

    /**
     * Registers a new AppUser from a DTO.
     */
    public AppUserResponseDTO registerUser(AppUserRequestDTO requestDTO) {
        if (appUserRepository.findByUsername(requestDTO.getUsername()).isPresent() ||
                appUserRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Username or email already in use");
        }

        AppUser newUser = appUserMapper.toEntity(requestDTO);
        newUser.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        newUser.setCreatedDate(LocalDateTime.now());

        AppUser savedUser = appUserRepository.save(newUser);

        String token = jwtService.generateToken(savedUser);
        AppUserResponseDTO responseDTO = appUserMapper.toResponseDTO(savedUser);
        responseDTO.setToken(token);

        return responseDTO;
    }

    /**
     * Authenticates a user with username/password and links their deviceUuid.
     */
    public AppUserResponseDTO loginWithPassword(AppUserLoginRequestDTO loginRequest) {
        // --- 1. FIND USER BY USERNAME OR EMAIL ---
        String identifier = loginRequest.getLoginIdentifier();

        // Find user by username OR email
        AppUser user = appUserRepository.findByUsername(identifier) //
                .or(() -> appUserRepository.findByEmail(identifier)) //
                .orElseThrow(() -> new EntityNotFoundException("Invalid credentials"));
        // --- END OF NEW LOGIC ---

        // --- 2. CHECK PASSWORD ---
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // Passwords match.
            user.setDeviceUuid(loginRequest.getDeviceUuid());
            user.setLastLoginDate(LocalDateTime.now());
            AppUser savedUser = appUserRepository.save(user);

            // --- 3. GENERATE TOKEN (unchanged) ---
            String token = jwtService.generateToken(savedUser);
            AppUserResponseDTO responseDTO = appUserMapper.toResponseDTO(savedUser);
            responseDTO.setToken(token);

            return responseDTO;
        } else {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    /**
     * Authenticates a user automatically using their deviceUuid.
     * (This method is UPDATED)
     */
    public AppUserResponseDTO loginWithUuid(UuidLoginRequestDTO loginRequest) {
        AppUser user = appUserRepository.findByDeviceUuid(loginRequest.getDeviceUuid())
                .orElseThrow(() -> new EntityNotFoundException("Device not recognized. Please log in."));

        user.setLastLoginDate(LocalDateTime.now());
        AppUser savedUser = appUserRepository.save(user);

        // --- 6. GENERATE TOKEN ---
        String token = jwtService.generateToken(savedUser);

        // --- 7. MAP TO DTO AND SET TOKEN ---
        AppUserResponseDTO responseDTO = appUserMapper.toResponseDTO(savedUser);
        responseDTO.setToken(token); // Add the token to the response

        return responseDTO; // Return DTO with token
    }
}
package com.pitt.hari_exercise_tracker.service;

import com.pitt.hari_exercise_tracker.dto.AppUserLoginRequestDTO;
import com.pitt.hari_exercise_tracker.dto.AppUserRequestDTO;
import com.pitt.hari_exercise_tracker.dto.AppUserResponseDTO;
import com.pitt.hari_exercise_tracker.dto.UuidLoginRequestDTO;
import com.pitt.hari_exercise_tracker.mapper.AppUserMapper;
import com.pitt.hari_exercise_tracker.models.AppUser;
import com.pitt.hari_exercise_tracker.models.UserDevice;
import com.pitt.hari_exercise_tracker.repository.AppUserRepository;
import com.pitt.hari_exercise_tracker.repository.UserDeviceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppUserMapper appUserMapper;
    private final JwtService jwtService;
    private final UserDeviceRepository userDeviceRepository;

    public AppUserService(AppUserRepository appUserRepository,
                          PasswordEncoder passwordEncoder,
                          AppUserMapper appUserMapper,
                          JwtService jwtService,
                          UserDeviceRepository userDeviceRepository) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.appUserMapper = appUserMapper;
        this.jwtService = jwtService;
        this.userDeviceRepository = userDeviceRepository;
    }

    /**
     * Registers a user AND creates their first device link.
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

        UserDevice userDevice = new UserDevice(savedUser, requestDTO.getDeviceUuid());
        userDeviceRepository.save(userDevice);

        String token = jwtService.generateToken(savedUser);
        AppUserResponseDTO responseDTO = appUserMapper.toResponseDTO(savedUser);
        responseDTO.setToken(token);
        return responseDTO;
    }

    public AppUserResponseDTO loginWithPassword(AppUserLoginRequestDTO loginRequest) {
        AppUser user = appUserRepository.findByUsername(loginRequest.getLoginIdentifier())
                .or(() -> appUserRepository.findByEmail(loginRequest.getLoginIdentifier()))
                .orElseThrow(() -> new EntityNotFoundException("Invalid credentials"));

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            user.setLastLoginDate(LocalDateTime.now());
            AppUser savedUser = appUserRepository.save(user);

            if (loginRequest.getDeviceUuid() != null) {
                UserDevice userDevice = userDeviceRepository.findByAppUserAndDeviceUuid(savedUser, loginRequest.getDeviceUuid())
                        .orElse(new UserDevice(savedUser, loginRequest.getDeviceUuid()));

                // --- 2. UPDATE THIS LINE ---
                userDevice.setLastUsed(LocalDateTime.now());
                userDeviceRepository.save(userDevice);
            }

            String token = jwtService.generateToken(savedUser);
            AppUserResponseDTO responseDTO = appUserMapper.toResponseDTO(savedUser);
            responseDTO.setToken(token);
            return responseDTO;
        } else {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    /**
     * Logs in the MOST RECENT user associated with this device.
     */
    public AppUserResponseDTO loginWithUuid(UuidLoginRequestDTO loginRequest) {
        UserDevice userDevice = userDeviceRepository.findFirstByDeviceUuidOrderByLastUsedDesc(loginRequest.getDeviceUuid())
                .orElseThrow(() -> new EntityNotFoundException("Device not recognized. Please log in."));

        AppUser user = userDevice.getAppUser();

        user.setLastLoginDate(LocalDateTime.now());
        userDevice.setLastUsed(LocalDateTime.now());
        appUserRepository.save(user);
        userDeviceRepository.save(userDevice);

        String token = jwtService.generateToken(user);
        AppUserResponseDTO responseDTO = appUserMapper.toResponseDTO(user);
        responseDTO.setToken(token);
        return responseDTO;
    }
}
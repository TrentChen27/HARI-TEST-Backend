package com.pitt.hari_exercise_tracker.mapper;

import com.pitt.hari_exercise_tracker.dto.AppUserResponseDTO;
import com.pitt.hari_exercise_tracker.dto.HealthCoachRequestDTO;
import com.pitt.hari_exercise_tracker.dto.HealthCoachResponseDTO;
import com.pitt.hari_exercise_tracker.models.AppUser;
import com.pitt.hari_exercise_tracker.models.HealthCoach;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Utility to map between HealthCoach DTOs and Entities.
 * We make this a @Component so Spring can inject it into our service.
 */
@Component
public class HealthCoachMapper {

    // We need a mapper for AppUser to handle the set of clients
    private final AppUserMapper appUserMapper;

    public HealthCoachMapper(AppUserMapper appUserMapper) {
        this.appUserMapper = appUserMapper;
    }

    public HealthCoach toEntity(HealthCoachRequestDTO dto) {
        HealthCoach entity = new HealthCoach();
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setSpecialization(dto.getSpecialization());
        entity.setBio(dto.getBio());
        // Password is set in the service (since it needs hashing)
        return entity;
    }

    public HealthCoachResponseDTO toResponseDTO(HealthCoach entity) {
        HealthCoachResponseDTO dto = new HealthCoachResponseDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setSpecialization(entity.getSpecialization());
        dto.setBio(entity.getBio());

        // Convert the Set<AppUser> to a Set<AppUserResponseDTO>
        if (entity.getClients() != null) {
            dto.setClients(entity.getClients().stream()
                    .map(appUserMapper::toResponseDTO)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }
}

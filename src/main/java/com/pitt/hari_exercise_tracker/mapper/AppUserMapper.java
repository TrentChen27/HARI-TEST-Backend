package com.pitt.hari_exercise_tracker.mapper;

import com.pitt.hari_exercise_tracker.dto.AppUserRequestDTO;
import com.pitt.hari_exercise_tracker.dto.AppUserResponseDTO;
import com.pitt.hari_exercise_tracker.models.AppUser;
import org.springframework.stereotype.Component;

/**
 * Utility to map between AppUser DTOs and Entities.
 * We make this a @Component so Spring can inject it.
 */
@Component
public class AppUserMapper {

    public AppUser toEntity(AppUserRequestDTO dto) {
        AppUser entity = new AppUser();
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
//        entity.setDeviceUuid(dto.getDeviceUuid());
        return entity;
    }

    public AppUserResponseDTO toResponseDTO(AppUser user) {
        AppUserResponseDTO dto = new AppUserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
//        dto.setDeviceUuid(user.getDeviceUuid());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setCreatedDate(user.getCreatedDate());
        dto.setLastLoginDate(user.getLastLoginDate());
        dto.setBioInfo(user.getBioInfo());
        dto.setHealthGoals(user.getHealthGoals());
        dto.setMedicalConditions(user.getMedicalConditions());
        return dto;
    }
}

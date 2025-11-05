package com.pitt.hari_exercise_tracker.mapper;

import com.pitt.hari_exercise_tracker.dto.ExerciseRecordRequestDTO;
import com.pitt.hari_exercise_tracker.dto.ExerciseRecordResponseDTO;
import com.pitt.hari_exercise_tracker.models.ExerciseRecord;
import com.pitt.hari_exercise_tracker.util.DurationParser;

import java.time.LocalDateTime;

/**
 * A utility class to map between DTOs and the ExerciseRecord Entity.
 * This keeps our conversion logic in one clean, testable place.
 */
public class ExerciseRecordMapper {

    /**
     * Converts a RequestDTO into an Entity.
     * If dateTime is null in the DTO, it defaults to Instant.now().
     */
    public static ExerciseRecord toEntity(ExerciseRecordRequestDTO dto) {
        ExerciseRecord entity = new ExerciseRecord();
        entity.setExerciseType(dto.getExerciseType());
        entity.setExerciseDuration(DurationParser.parseToMinutes(dto.getExerciseDuration()));
        entity.setExerciseLocation(dto.getExerciseLocation());
        entity.setExerciseNotes(dto.getExerciseNotes());
        entity.setExerciseIntensity(dto.getExerciseIntensity());
        if (dto.getCaloriesBurned() != null) {
            entity.setCaloriesBurned(Double.valueOf(dto.getCaloriesBurned()));
        } else {
            entity.setCaloriesBurned(0.0);
        }

        if (dto.getDateTime() != null) {
            if (dto.getDateTime().isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Exercise date cannot be in the future");
            }
            entity.setDateTime(dto.getDateTime());
        } else {
            entity.setDateTime(LocalDateTime.now());
        }

        entity.setSets(dto.getSets());
        entity.setReps(dto.getReps());
        entity.setWeightKg(dto.getWeightKg());
        entity.setImageUrl(dto.getImageUrl());
        return entity;
    }

    /**
     * Converts an Entity into a ResponseDTO.
     */
    public static ExerciseRecordResponseDTO toResponseDTO(ExerciseRecord entity) {
        ExerciseRecordResponseDTO dto = new ExerciseRecordResponseDTO();
        dto.setId(entity.getId());
        dto.setExerciseType(entity.getExerciseType());
        dto.setExerciseDuration(entity.getExerciseDuration());
        dto.setExerciseLocation(entity.getExerciseLocation());
        dto.setExerciseNotes(entity.getExerciseNotes());
        dto.setExerciseIntensity(entity.getExerciseIntensity());
        dto.setCaloriesBurned(String.valueOf(entity.getCaloriesBurned()));
        dto.setDateTime(entity.getDateTime());
        dto.setSets(entity.getSets());
        dto.setReps(entity.getReps());
        dto.setWeightKg(entity.getWeightKg());
        dto.setImageUrl(entity.getImageUrl());

        // Add the associated username (but not the whole user object)
        if (entity.getAppUser() != null) {
            dto.setUsername(entity.getAppUser().getUsername());
        }
        return dto;
    }

    /**
     * Updates an existing entity from a DTO.
     * Only updates dateTime if it's explicitly provided.
     */
    public static void updateEntityFromDto(ExerciseRecord entity, ExerciseRecordRequestDTO dto) {
        entity.setExerciseType(dto.getExerciseType());
        entity.setExerciseDuration(DurationParser.parseToMinutes(dto.getExerciseDuration()));
        entity.setExerciseLocation(dto.getExerciseLocation());
        entity.setExerciseNotes(dto.getExerciseNotes());
        entity.setExerciseIntensity(dto.getExerciseIntensity());
        if (dto.getCaloriesBurned() != null) {
            entity.setCaloriesBurned(Double.valueOf(dto.getCaloriesBurned()));
        } else {
            entity.setCaloriesBurned(0.0);
        }

        if (dto.getDateTime() != null) {
            if (dto.getDateTime().isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Exercise date cannot be in the future");
            }
            entity.setDateTime(dto.getDateTime());
        }

        entity.setSets(dto.getSets());
        entity.setReps(dto.getReps());
        entity.setWeightKg(dto.getWeightKg());
        entity.setImageUrl(dto.getImageUrl());
    }
}


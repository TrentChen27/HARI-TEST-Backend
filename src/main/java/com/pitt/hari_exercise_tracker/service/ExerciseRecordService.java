package com.pitt.hari_exercise_tracker.service;

import com.pitt.hari_exercise_tracker.dto.ExerciseRecordRequestDTO;
import com.pitt.hari_exercise_tracker.dto.ExerciseRecordResponseDTO;
import com.pitt.hari_exercise_tracker.mapper.ExerciseRecordMapper;
import com.pitt.hari_exercise_tracker.models.AppUser;
import com.pitt.hari_exercise_tracker.models.ExerciseRecord;
import com.pitt.hari_exercise_tracker.repository.AppUserRepository;
import com.pitt.hari_exercise_tracker.repository.ExerciseRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseRecordService {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final AppUserRepository appUserRepository;

    public ExerciseRecordService(ExerciseRecordRepository exerciseRecordRepository, AppUserRepository appUserRepository) {
        this.exerciseRecordRepository = exerciseRecordRepository;
        this.appUserRepository = appUserRepository;
    }

    /**
     * Creates a new exercise record from a DTO.
     */
    public ExerciseRecordResponseDTO createExerciseRecord(ExerciseRecordRequestDTO dto, Long userId) {
        // Find the user
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Convert DTO to Entity
        ExerciseRecord exerciseRecord = ExerciseRecordMapper.toEntity(dto);

        // Link the record to the user and save
        exerciseRecord.setAppUser(user);
        ExerciseRecord savedRecord = exerciseRecordRepository.save(exerciseRecord);

        // Convert saved Entity back to a *safe* ResponseDTO
        return ExerciseRecordMapper.toResponseDTO(savedRecord);
    }

    /**
     * Updates an existing exercise record from a DTO.
     */
    public ExerciseRecordResponseDTO updateExerciseRecord(Long recordId, ExerciseRecordRequestDTO dto) {
        // Find the existing record
        ExerciseRecord existingRecord = exerciseRecordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Record not found with id: " + recordId));

        // Update the entity's fields from the DTO
        ExerciseRecordMapper.updateEntityFromDto(existingRecord, dto);

        // Save the updated entity
        ExerciseRecord savedRecord = exerciseRecordRepository.save(existingRecord);

        // Convert to ResponseDTO
        return ExerciseRecordMapper.toResponseDTO(savedRecord);
    }

    // Helper to get the start of today in UTC
    private java.time.Instant getStartOfTodayUtc() {
        return LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    /**
     * Gets all records for a user for *today*.
     */
    public List<ExerciseRecordResponseDTO> getRecordsForToday(Long userId) {
        java.time.Instant startOfDay = getStartOfTodayUtc();
        java.time.Instant endOfDay = startOfDay.plusSeconds(24 * 60 * 60); // 24 hours

        return exerciseRecordRepository.findByAppUserIdAndDateTimeBetweenOrderByDateTimeDesc(userId, startOfDay, endOfDay)
                .stream()
                .map(ExerciseRecordMapper::toResponseDTO) // Convert each entity to a DTO
                .collect(Collectors.toList());
    }

    /**
     * Gets all records for a user *before* today.
     */
    public List<ExerciseRecordResponseDTO> getRecordHistory(Long userId) {
        java.time.Instant startOfDay = getStartOfTodayUtc();

        return exerciseRecordRepository.findByAppUserIdAndDateTimeBeforeOrderByDateTimeDesc(userId, startOfDay)
                .stream()
                .map(ExerciseRecordMapper::toResponseDTO) // Convert each entity to a DTO
                .collect(Collectors.toList());
    }

    /**
     * Gets *all* records for a user.
     */
    public List<ExerciseRecordResponseDTO> getAllRecordsForUser(Long userId) {
        return exerciseRecordRepository.findByAppUserIdOrderByDateTimeDesc(userId)
                .stream()
                .map(ExerciseRecordMapper::toResponseDTO) // Convert each entity to a DTO
                .collect(Collectors.toList());
    }
}


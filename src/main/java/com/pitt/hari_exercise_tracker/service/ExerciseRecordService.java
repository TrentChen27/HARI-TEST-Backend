package com.pitt.hari_exercise_tracker.service;

import com.pitt.hari_exercise_tracker.dto.ExerciseRecordRequestDTO;
import com.pitt.hari_exercise_tracker.dto.ExerciseRecordResponseDTO;
import com.pitt.hari_exercise_tracker.dto.ExerciseReportDTO;
import com.pitt.hari_exercise_tracker.mapper.ExerciseRecordMapper;
import com.pitt.hari_exercise_tracker.models.AppUser;
import com.pitt.hari_exercise_tracker.models.ExerciseRecord;
import com.pitt.hari_exercise_tracker.repository.AppUserRepository;
import com.pitt.hari_exercise_tracker.repository.ExerciseRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private LocalDateTime getStartOfTodayLocal() {
        return LocalDate.now().atStartOfDay();
    }
    /**
     * Gets all records for a user for *today*.
     */
    public List<ExerciseRecordResponseDTO> getRecordsForToday(Long userId) {
        LocalDateTime startOfDay = getStartOfTodayLocal();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        return exerciseRecordRepository.findByAppUserIdAndDateTimeBetweenOrderByDateTimeDesc(userId, startOfDay, endOfDay)
                .stream()
                .map(ExerciseRecordMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets all records for a user *before* today.
     */
    public List<ExerciseRecordResponseDTO> getRecordHistory(Long userId) {
        LocalDateTime startOfDay = getStartOfTodayLocal();

        return exerciseRecordRepository.findByAppUserIdAndDateTimeBeforeOrderByDateTimeDesc(userId, startOfDay)
                .stream()
                .map(ExerciseRecordMapper::toResponseDTO)
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

    /**
     * Deletes an exercise record by ID.
     */
    public void deleteExerciseRecord(Long recordId) {
        ExerciseRecord existingRecord = exerciseRecordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Record not found with id: " + recordId));

        exerciseRecordRepository.delete(existingRecord);
    }

    /**
     * Gets a 7-day report for a user.
     */
    public ExerciseReportDTO get7DayReport(Long userId) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime now = LocalDateTime.now();

        List<ExerciseRecord> records = exerciseRecordRepository
                .findByAppUserIdAndDateTimeAfterOrderByDateTimeDesc(userId, sevenDaysAgo);

        int totalExercises = records.size();
        int totalMinutes = records.stream()
                .mapToInt(record -> record.getExerciseDuration() != null ? record.getExerciseDuration() : 0)
                .sum();

        String startDate = sevenDaysAgo.toString();
        String endDate = now.toString();

        return new ExerciseReportDTO(totalExercises, totalMinutes, startDate, endDate);
    }


}


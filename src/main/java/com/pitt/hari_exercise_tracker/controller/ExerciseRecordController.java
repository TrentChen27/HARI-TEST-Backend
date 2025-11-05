package com.pitt.hari_exercise_tracker.controller;

import com.pitt.hari_exercise_tracker.dto.ExerciseRecordRequestDTO;
import com.pitt.hari_exercise_tracker.dto.ExerciseRecordResponseDTO;
import com.pitt.hari_exercise_tracker.dto.ExerciseReportDTO;
import com.pitt.hari_exercise_tracker.service.ExerciseRecordService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class ExerciseRecordController {

    private final ExerciseRecordService exerciseRecordService;

    public ExerciseRecordController(ExerciseRecordService exerciseRecordService) {
        this.exerciseRecordService = exerciseRecordService;
    }

    /**
     * POST /api/records/{userId}
     * Creates a new exercise record from a DTO.
     */
    @PostMapping("/{userId}")
    public ResponseEntity<ExerciseRecordResponseDTO> createRecord(@PathVariable Long userId, @RequestBody ExerciseRecordRequestDTO dto) {
        try {
            ExerciseRecordResponseDTO createdRecord = exerciseRecordService.createExerciseRecord(dto, userId);
            return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<ExerciseRecordResponseDTO> updateRecord(@PathVariable Long recordId, @RequestBody ExerciseRecordRequestDTO dto) {
        try {
            ExerciseRecordResponseDTO savedRecord = exerciseRecordService.updateExerciseRecord(recordId, dto);
            return new ResponseEntity<>(savedRecord, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * GET /api/records/user/{userId}/today
     * Gets a *safe* list of records for today.
     */
    @GetMapping("/user/{userId}/today")
    public ResponseEntity<List<ExerciseRecordResponseDTO>> getRecordsForToday(@PathVariable Long userId) {
        List<ExerciseRecordResponseDTO> records = exerciseRecordService.getRecordsForToday(userId);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    /**
     * GET /api/records/user/{userId}/report/7-days
     * Gets a 7-day exercise report for a user.
     */
    @GetMapping("/user/{userId}/report/7-days")
    public ResponseEntity<ExerciseReportDTO> get7DayReport(@PathVariable Long userId) {
        ExerciseReportDTO report = exerciseRecordService.get7DayReport(userId);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }


    /**
     * GET /api/records/user/{userId}/history
     * Gets a *safe* list of record history.
     */
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<ExerciseRecordResponseDTO>> getRecordHistory(@PathVariable Long userId) {
        List<ExerciseRecordResponseDTO> records = exerciseRecordService.getRecordHistory(userId);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    /**
     * GET /api/records/user/{userId}/all
     * Gets a *safe* list of all records for a user.
     */
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<ExerciseRecordResponseDTO>> getAllRecordsForUser(@PathVariable Long userId) {
        List<ExerciseRecordResponseDTO> records = exerciseRecordService.getAllRecordsForUser(userId);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    /**
     * DELETE /api/records/{recordId}
     * Deletes an exercise record.
     */
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long recordId) {
        try {
            exerciseRecordService.deleteExerciseRecord(recordId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}


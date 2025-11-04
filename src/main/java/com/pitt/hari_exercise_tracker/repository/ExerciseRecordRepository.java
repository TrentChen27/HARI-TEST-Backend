package com.pitt.hari_exercise_tracker.repository;

import com.pitt.hari_exercise_tracker.models.AppUser;
import com.pitt.hari_exercise_tracker.models.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {

    // "SELECT * FROM exercise_records WHERE appUser = ?"
    List<ExerciseRecord> findByAppUser(AppUser appUser);

    // "SELECT * FROM exercise_records WHERE appUser.id = ? AND dateTime BETWEEN ? AND ? ORDER BY dateTime DESC"
    List<ExerciseRecord> findByAppUserIdAndDateTimeBetweenOrderByDateTimeDesc(Long userId, Instant startOfDay, Instant endOfDay);

    // "SELECT * FROM exercise_records WHERE appUser.id = ? AND dateTime < ? ORDER BY dateTime DESC"
    List<ExerciseRecord> findByAppUserIdAndDateTimeBeforeOrderByDateTimeDesc(Long userId, Instant startOfDay);

    // "SELECT * FROM exercise_records WHERE appUser.id = ? ORDER BY dateTime DESC"
    List<ExerciseRecord> findByAppUserIdOrderByDateTimeDesc(Long userId);

    // "SELECT * FROM exercise_records WHERE appUser.id = ? ORDER BY dateTime DESC LIMIT 1"
    Optional<ExerciseRecord> findFirstByAppUserIdOrderByDateTimeDesc(Long userId);
}

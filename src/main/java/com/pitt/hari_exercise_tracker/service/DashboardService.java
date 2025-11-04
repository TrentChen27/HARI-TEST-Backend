package com.pitt.hari_exercise_tracker.service;

import com.pitt.hari_exercise_tracker.dto.DashboardUserDTO;
import com.pitt.hari_exercise_tracker.models.AppUser;
import com.pitt.hari_exercise_tracker.models.ExerciseRecord;
import com.pitt.hari_exercise_tracker.repository.AppUserRepository;
import com.pitt.hari_exercise_tracker.repository.ExerciseRecordRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final AppUserRepository appUserRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;

    public DashboardService(AppUserRepository appUserRepository, ExerciseRecordRepository exerciseRecordRepository) {
        this.appUserRepository = appUserRepository;
        this.exerciseRecordRepository = exerciseRecordRepository;
    }

    /**
     * Gets the list of all app users for the coach's dashboard.
     * Calculates alert status and sorts according to project requirements.
     */
    public List<DashboardUserDTO> getDashboardData() {
        // 1. Get all users
        List<AppUser> allUsers = appUserRepository.findAll();

        // 2. Define the "alert" threshold (2 days ago)
        Instant alertThreshold = Instant.now().minus(2, ChronoUnit.DAYS);

        // 3. Map users to DTOs
        List<DashboardUserDTO> dashboardUsers = allUsers.stream().map(user -> {
            // Find the last record for this user
            Optional<ExerciseRecord> lastRecordOpt = exerciseRecordRepository.findFirstByAppUserIdOrderByDateTimeDesc(user.getId());

            Instant lastReportTime = null;
            boolean alert = true; // Default to alert

            if (lastRecordOpt.isPresent()) {
                lastReportTime = lastRecordOpt.get().getDateTime();
                // If their last report was *after* the threshold, they are NOT on alert
                if (lastReportTime.isAfter(alertThreshold)) {
                    alert = false;
                }
            }
            // If they have no record (lastReportTime is null), alert remains true.

            return new DashboardUserDTO(
                    user.getId(),
                    user.getUsername(),
                    alert,
                    lastReportTime
            );
        }).collect(Collectors.toList());

        // 4. Sort the list
        dashboardUsers.sort(new DashboardUserComparator());

        return dashboardUsers;
    }

    /**
     * Custom Comparator to sort the dashboard.
     * Rules:
     * 1. Users with alerts (true) come before users without (false).
     * 2. If both users have alerts, the one who reported *longer* ago comes first (nulls, then ascending time).
     * 3. If neither user has an alert, the one who reported *most recently* comes first (descending time).
     */
    private static class DashboardUserComparator implements Comparator<DashboardUserDTO> {
        @Override
        public int compare(DashboardUserDTO u1, DashboardUserDTO u2) {
            // Rule 1: Alerts come first
            if (u1.isAlert() && !u2.isAlert()) {
                return -1; // u1 comes before u2
            }
            if (!u1.isAlert() && u2.isAlert()) {
                return 1; // u2 comes before u1
            }

            // At this point, both users have the same alert status.

            // Rule 2: Both users HAVE alerts
            if (u1.isAlert()) {
                // If one user has *never* reported, they come first
                if (u1.getLastTimeOfReport() == null && u2.getLastTimeOfReport() != null) return -1;
                if (u1.getLastTimeOfReport() != null && u2.getLastTimeOfReport() == null) return 1;
                if (u1.getLastTimeOfReport() == null) return 0; // Both never reported

                // Both have reported, sort by oldest report first (ascending)
                return u1.getLastTimeOfReport().compareTo(u2.getLastTimeOfReport());
            }

            // Rule 3: Both users do NOT have alerts
            // Sort by most recent report first (descending)
            return u2.getLastTimeOfReport().compareTo(u1.getLastTimeOfReport());
        }
    }
}

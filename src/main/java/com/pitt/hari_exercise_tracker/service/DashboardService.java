package com.pitt.hari_exercise_tracker.service;

import com.pitt.hari_exercise_tracker.dto.DashboardUserDTO;
import com.pitt.hari_exercise_tracker.models.AppUser;
import com.pitt.hari_exercise_tracker.models.ExerciseRecord;
import com.pitt.hari_exercise_tracker.repository.AppUserRepository;
import com.pitt.hari_exercise_tracker.repository.ExerciseRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final AppUserRepository appUserRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;

    private final Integer MAX_DAYS_NO_REPORT_ALERT = 7;
    private final Integer MID_DAYS_NO_REPORT_ALERT = 4;
    private final Integer LOW_DAYS_NO_REPORT_ALERT = 2;

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
        LocalDateTime alertThreshold = LocalDateTime.now().minusDays(2);
        LocalDateTime now = LocalDateTime.now();

        // 3. Map users to DTOs
        List<DashboardUserDTO> dashboardUsers = allUsers.stream().map(user -> {
            // Find the last record for this user
            Optional<ExerciseRecord> lastRecordOpt = exerciseRecordRepository.findFirstByAppUserIdOrderByDateTimeDesc(user.getId());

            LocalDateTime lastReportTime = null;
            boolean alert = true; // Default to alert
            String alertLevel = "";

            if (lastRecordOpt.isPresent()) {
                lastReportTime = lastRecordOpt.get().getDateTime();
                // Calculate days since last report
                long daysSinceReport = java.time.Duration.between(lastReportTime, now).toDays();

                if (daysSinceReport >= MAX_DAYS_NO_REPORT_ALERT) {
                    alertLevel = "high";
                } else if (daysSinceReport >= MID_DAYS_NO_REPORT_ALERT) {
                    alertLevel = "medium";
                } else if (daysSinceReport >= LOW_DAYS_NO_REPORT_ALERT) {
                    alertLevel = "low";
                } else {
                    alert = false;
                    alertLevel = "";
                }
            } else {
                // No reports yet - check account age
                long daysSinceCreation = java.time.Duration.between(user.getCreatedDate(), now).toDays();

                if (user.getCreatedDate().isAfter(alertThreshold)) {
                    // Account is newer than 2 days - no alert yet
                    alert = false;
                    alertLevel = "";
                } else if (daysSinceCreation >= 7) {
                    alert = true;
                    alertLevel = "high";
                } else if (daysSinceCreation >= 4) {
                    alert = true;
                    alertLevel = "medium";
                } else {
                    alert = true;
                    alertLevel = "low";
                }
            }

            return new DashboardUserDTO(
                    user.getId(),
                    user.getUsername(),
                    alert,
                    alertLevel,
                    lastReportTime,
                    user.getLastLoginDate(),
                    user.getCreatedDate()
            );
        }).collect(Collectors.toList());

        // 4. Sort the list
        dashboardUsers.sort(new DashboardUserComparator());

        return dashboardUsers;
    }

    /**
     * Custom Comparator to sort the dashboard.
     * Rules:
     * 1. Users with alerts come first.
     * 2. Among users with alerts:
     *   - Users who have never reported come first, sorted by "active days" (accountCreation to lastLogin) descending.
     *   - Then users who have reported, sorted by oldest report first (ascending).
     * 3. Among users without alerts:
     *  - Sorted by most recent report first (descending).
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
                // If both have never reported, compare by "active days"
                if (u1.getLastTimeOfReport() == null && u2.getLastTimeOfReport() == null) {
                    // User with more "active days" (lastLogin - accountCreation) comes first
                    long u1ActiveDays = java.time.Duration.between(u1.getAccountCreationTime(), u1.getLastLoginTime()).toDays();
                    long u2ActiveDays = java.time.Duration.between(u2.getAccountCreationTime(), u2.getLastLoginTime()).toDays();
                    return Long.compare(u2ActiveDays, u1ActiveDays); // Descending order
                }
                // If one user has never reported, they come first
                if (u1.getLastTimeOfReport() == null) return -1;
                if (u2.getLastTimeOfReport() == null) return 1;
                // Both have reported, sort by oldest report first (ascending)
                return u1.getLastTimeOfReport().compareTo(u2.getLastTimeOfReport());
            }
            // Rule 3: Both users do NOT have alerts
            // Handle null cases first
            if (u1.getLastTimeOfReport() == null && u2.getLastTimeOfReport() == null) {
                return 0; // Both null, consider equal
            }
            if (u1.getLastTimeOfReport() == null) return 1; // Null goes to end
            if (u2.getLastTimeOfReport() == null) return -1; // Null goes to end

            // Sort by most recent report first (descending)
            return u2.getLastTimeOfReport().compareTo(u1.getLastTimeOfReport());
        }
    }
}

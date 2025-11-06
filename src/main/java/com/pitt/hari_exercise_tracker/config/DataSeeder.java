package com.pitt.hari_exercise_tracker.config;

import com.github.javafaker.Faker;
import com.pitt.hari_exercise_tracker.models.AppUser;
import com.pitt.hari_exercise_tracker.models.ExerciseRecord;
import com.pitt.hari_exercise_tracker.models.HealthCoach;
import com.pitt.hari_exercise_tracker.models.UserDevice;
import com.pitt.hari_exercise_tracker.repository.AppUserRepository;
import com.pitt.hari_exercise_tracker.repository.ExerciseRecordRepository;
import com.pitt.hari_exercise_tracker.repository.HealthCoachRepository;
import com.pitt.hari_exercise_tracker.repository.UserDeviceRepository;
import com.pitt.hari_exercise_tracker.util.DurationParser;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    // Inject all the repositories we need
    private final AppUserRepository appUserRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final HealthCoachRepository healthCoachRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only run if the database is empty
        if (appUserRepository.count() == 0 && healthCoachRepository.count() == 0) {
            System.out.println("No data found. Seeding database...");
            Faker faker = new Faker();

            // === 1. CREATE A HEALTH COACH ===
            HealthCoach coach = new HealthCoach();
            coach.setUsername("coach_hari");
            coach.setEmail("coach@hari.com");
            coach.setPassword(passwordEncoder.encode("password"));
            coach.setFirstName("Hari");
            coach.setLastName("Pitt");
            coach.setSpecialization("Cardio & Rehabilitation");
            healthCoachRepository.save(coach);

            // === 2. CREATE 10 APP USERS WITH RECORDS ===
            List<AppUser> users = new ArrayList<>();

            // User 1: The "perfect" user (User 8 from your test)
            AppUser user8 = new AppUser();
            user8.setUsername("test123");
            user8.setEmail("test@gg.com");
            user8.setPassword(passwordEncoder.encode("password")); // Set a known password
            user8.setCreatedDate(LocalDateTime.now().minusDays(30));
            user8.setLastLoginDate(LocalDateTime.now());
            users.add(user8);

            // User 2: A user with a > 3-day gap (will trigger alert)
            AppUser alertUser = new AppUser();
            alertUser.setUsername("alert_user");
            alertUser.setEmail(faker.internet().emailAddress());
            alertUser.setPassword(passwordEncoder.encode("password"));
            alertUser.setCreatedDate(LocalDateTime.now().minusDays(30));
            users.add(alertUser);

            // User 3: A user with no reports at all (will trigger alert)
            AppUser noReportUser = new AppUser();
            noReportUser.setUsername("no_report_user");
            noReportUser.setEmail(faker.internet().emailAddress());
            noReportUser.setPassword(passwordEncoder.encode("password"));
            noReportUser.setCreatedDate(LocalDateTime.now().minusDays(30));
            users.add(noReportUser);

            // Create 7 more "normal" users
            for (int i = 0; i < 7; i++) {
                AppUser user = new AppUser();
                user.setUsername(faker.name().username());
                user.setEmail(faker.internet().emailAddress());
                user.setPassword(passwordEncoder.encode("password"));
                user.setCreatedDate(LocalDateTime.now().minusDays(30));
                users.add(user);
            }

            appUserRepository.saveAll(users);

            // === 3. LINK YOUR PHONE'S ID TO USER 8 ===
            // This lets you auto-login as 'test123'
            // We use the ID you gave me in the error log
            UserDevice user8Device = new UserDevice(user8, "016ccfce66cf9d4a");
            userDeviceRepository.save(user8Device);


            // === 4. GENERATE EXERCISE RECORDS ===
            List<ExerciseRecord> allRecords = new ArrayList<>();
            for (AppUser user : users) {

                // --- This user has NO records ---
                if (user.getUsername().equals("no_report_user")) {
                    continue;
                }

                // Loop for the past 14 days
                for (int day = 0; day < 14; day++) {

                    // --- This user has a 3-day gap (from day 2 to 4) ---
                    if (user.getUsername().equals("alert_user") && day > 1 && day < 5) {
                        continue; // Skip these days to create a gap
                    }

                    // For all other users, create 2 records per day
                    int recordsPerDay = 2;
                    for (int j = 0; j < recordsPerDay; j++) {
                        ExerciseRecord record = new ExerciseRecord();
                        record.setAppUser(user);
                        record.setExerciseType(faker.team().sport());
                        record.setExerciseDuration(DurationParser.parseToMinutes(faker.number().numberBetween(15, 60) + " mins"));
                        record.setExerciseLocation(faker.bool().bool() ? "inside" : "outside");

                        // Set a timestamp for that day
                        LocalDateTime timestamp = LocalDateTime.now()
                                .minusDays(day)
                                .withHour(faker.number().numberBetween(8, 20))
                                .withMinute(faker.number().numberBetween(0, 59));
                        record.setDateTime(timestamp);

                        allRecords.add(record);
                    }
                }
            }

            exerciseRecordRepository.saveAll(allRecords);

            System.out.println("Database seeded with 1 coach, 10 users, and " + allRecords.size() + " records.");

        } else {
            System.out.println("Database already contains data. Skipping seeding.");
        }
    }
}
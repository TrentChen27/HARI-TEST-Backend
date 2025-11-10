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
// Assuming you have this utility class from your file
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

    private final AppUserRepository appUserRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final HealthCoachRepository healthCoachRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final PasswordEncoder passwordEncoder;

    // A list of realistic exercise types
    private static final List<String> EXERCISE_TYPES = List.of(
            "Running", "Weightlifting", "Yoga", "Stretching", "Walking",
            "Cycling", "Swimming", "Basketball", "Rowing", "Elliptical"
    );

    @Override
    public void run(String... args) throws Exception {
        // Only run if the database is empty
        if (appUserRepository.count() > 0 || healthCoachRepository.count() > 0) {
            System.out.println("Database already contains data. Skipping seeding.");
            return;
        }

        System.out.println("No data found. Seeding database for demo...");
        Faker faker = new Faker();

        HealthCoach coach = new HealthCoach();
        coach.setUsername("coach_hari");
        coach.setEmail("coach@hari.com");
        coach.setPassword(passwordEncoder.encode("password"));
        coach.setFirstName("Hari");
        coach.setLastName("Pitt");
        coach.setSpecialization("Cardio & Rehabilitation");
        healthCoachRepository.save(coach);

        List<AppUser> users = new ArrayList<>();


        AppUser demoUser = createUser(faker, "demo_user", "demo@app.com");
        users.add(demoUser);

        AppUser noReportUser = createUser(faker, "no_reports_user", faker.internet().emailAddress());
        users.add(noReportUser);


        AppUser fiveDayGapUser = createUser(faker, "five_day_gap_user", faker.internet().emailAddress());
        users.add(fiveDayGapUser);

        AppUser threeDayGapUser = createUser(faker, "three_day_gap_user", faker.internet().emailAddress());
        users.add(threeDayGapUser);

        AppUser twoDayGapUser = createUser(faker, "two_day_gap_user", faker.internet().emailAddress());
        users.add(twoDayGapUser);

        AppUser oneDayGapUser = createUser(faker, "one_day_gap_user", faker.internet().emailAddress());
        users.add(oneDayGapUser);


        for (int i = 0; i < 4; i++) {
            users.add(createUser(faker, faker.name().username(), faker.internet().emailAddress()));
        }

        appUserRepository.saveAll(users);

        UserDevice user8Device = new UserDevice(demoUser, "016ccfce66cf9d4a");
        userDeviceRepository.save(user8Device);

        List<ExerciseRecord> allRecords = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (AppUser user : users) {

            for (int day = 0; day < 14; day++) {

                String username = user.getUsername();
                if (username.equals("no_reports_user")) continue; // 0 records
                if (username.equals("five_day_gap_user") && day < 5) continue; // Skips today + 4 past days
                if (username.equals("three_day_gap_user") && day < 3) continue; // Skips today + 2 past days
                if (username.equals("two_day_gap_user") && day < 2) continue; // Skips today + 1 past day
                if (username.equals("one_day_gap_user") && day < 1) continue; // Skips today

                int recordsPerDay = faker.number().numberBetween(2, 4); // ~2.5 avg

                if (day == 0 && (username.equals("demo_user") || !username.contains("_gap_"))) {
                    recordsPerDay = faker.number().numberBetween(1, 3);
                }

                for (int j = 0; j < recordsPerDay; j++) {
                    allRecords.add(createRecord(faker, user, now.minusDays(day)));
                }
            }
        }

        exerciseRecordRepository.saveAll(allRecords);

        System.out.println("Database seeded with 1 coach, 10 users, and " + allRecords.size() + " records.");
    }

    /**
     * Helper method to create a new AppUser
     */
    private AppUser createUser(Faker faker, String username, String email) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password"));
        user.setCreatedDate(LocalDateTime.now().minusDays(30));
        return user;
    }

    /**
     * Helper method to create a new ExerciseRecord
     */
    private ExerciseRecord createRecord(Faker faker, AppUser user, LocalDateTime timestamp) {
        ExerciseRecord record = new ExerciseRecord();
        record.setAppUser(user);
        record.setExerciseType(EXERCISE_TYPES.get(faker.number().numberBetween(0, EXERCISE_TYPES.size())));

        String durationString = faker.number().numberBetween(15, 60) + " mins";
        record.setExerciseDuration(DurationParser.parseToMinutes(durationString));

        record.setExerciseLocation(faker.bool().bool() ? "inside" : "outside");

        LocalDateTime finalTimestamp = timestamp
                .withHour(faker.number().numberBetween(8, 20))
                .withMinute(faker.number().numberBetween(0, 59))
                .withSecond(faker.number().numberBetween(0, 59));
        record.setDateTime(finalTimestamp);

        return record;
    }
}
package com.pitt.hari_exercise_tracker.repository;

import com.pitt.hari_exercise_tracker.models.HealthCoach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthCoachRepository extends JpaRepository<HealthCoach, Long> {

    // "SELECT * FROM health_coaches WHERE username = ?"
    Optional<HealthCoach> findByUsername(String username);

    // "SELECT * FROM health_coaches WHERE email = ?"
    Optional<HealthCoach> findByEmail(String email);
}

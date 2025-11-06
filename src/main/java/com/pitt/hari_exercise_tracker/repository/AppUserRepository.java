package com.pitt.hari_exercise_tracker.repository;

import com.pitt.hari_exercise_tracker.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    // "SELECT * FROM app_users WHERE username = ?"
    Optional<AppUser> findByUsername(String username);

    // "SELECT * FROM app_users WHERE email = ?"
    Optional<AppUser> findByEmail(String email);

//    // "SELECT * FROM app_users WHERE device_uuid = ?"
//    Optional<AppUser> findByDeviceUuid(String deviceUuid);

}

package com.pitt.hari_exercise_tracker.repository;

import com.pitt.hari_exercise_tracker.models.AppUser;
import com.pitt.hari_exercise_tracker.models.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    // Finds a specific user-device link
    Optional<UserDevice> findByAppUserAndDeviceUuid(AppUser appUser, String deviceUuid);

    // Finds the MOST RECENT user for a given device (for auto-login)
    Optional<UserDevice> findFirstByDeviceUuidOrderByLastUsedDesc(String deviceUuid);
}
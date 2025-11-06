package com.pitt.hari_exercise_tracker.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_devices")
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser appUser;

    @Column(nullable = false)
    private String deviceUuid;

    @Column(nullable = false)
    private LocalDateTime lastUsed; // We'll use this for the "auto-login" logic

    public UserDevice(AppUser appUser, String deviceUuid) {
        this.appUser = appUser;
        this.deviceUuid = deviceUuid;
        this.lastUsed = LocalDateTime.now();
    }
}
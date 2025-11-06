package com.pitt.hari_exercise_tracker.controller;

import com.pitt.hari_exercise_tracker.dto.DashboardUserDTO;
import com.pitt.hari_exercise_tracker.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * GET /api/dashboard
     * Gets all user data for the coach's dashboard, sorted with alerts.
     */
    @GetMapping
    public ResponseEntity<List<DashboardUserDTO>> getDashboardData() {
        List<DashboardUserDTO> dashboardData = dashboardService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }
}

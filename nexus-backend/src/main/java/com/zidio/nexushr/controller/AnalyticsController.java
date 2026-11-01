package com.zidio.nexushr.controller;

import com.zidio.nexushr.dto.WorkforceAnalyticsResponse;
import com.zidio.nexushr.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // 1. GET: /api/analytics/workforce-summary - Real-time workforce metrics for Executive Dashboards
    @GetMapping("/workforce-summary")
    @PreAuthorize("hasRole('HR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<WorkforceAnalyticsResponse> getWorkforceSummary() {
        return ResponseEntity.ok(analyticsService.getWorkforceAnalytics());
    }
}

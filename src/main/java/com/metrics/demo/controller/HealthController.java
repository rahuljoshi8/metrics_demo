package com.metrics.demo.controller;



import com.metrics.demo.service.GitHubActionsService;
import com.metrics.demo.service.PagerDutyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for monitoring external service connectivity.
 *
 * @author Technical Lead Assignment
 */
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    private final PagerDutyService pagerDutyService;
    private final GitHubActionsService gitHubActionsService;

    @GetMapping("/external-services")
    @Operation(summary = "Check External Services Health",
            description = "Checks connectivity to PagerDuty and GitHub Actions APIs")
    public ResponseEntity<Map<String, Object>> checkExternalServices() {

        Map<String, Object> health = new HashMap<>();

        // Check PagerDuty
        boolean pagerDutyHealthy = pagerDutyService.isHealthy();
        health.put("pagerduty", Map.of(
                "status", pagerDutyHealthy ? "UP" : "DOWN",
                "healthy", pagerDutyHealthy
        ));

        // Check GitHub Actions
        boolean githubHealthy = gitHubActionsService.isHealthy();
        health.put("github_actions", Map.of(
                "status", githubHealthy ? "UP" : "DOWN",
                "healthy", githubHealthy
        ));

        // Overall status
        boolean overallHealthy = pagerDutyHealthy && githubHealthy;
        health.put("overall", Map.of(
                "status", overallHealthy ? "UP" : "DEGRADED",
                "healthy", overallHealthy
        ));

        return ResponseEntity.ok(health);
    }
}
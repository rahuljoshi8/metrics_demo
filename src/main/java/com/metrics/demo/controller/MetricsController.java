package com.metrics.demo.controller;


import com.metrics.demo.dto.response.ChangeFailureRateResponse;
import com.metrics.demo.dto.response.DashboardResponse;
import com.metrics.demo.dto.response.MTTRResponse;
import com.metrics.demo.service.GitHubActionsService;
import com.metrics.demo.service.MetricsCalculationService;
import com.metrics.demo.service.PagerDutyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST controller for metrics endpoints.
 *
 * Provides endpoints to calculate and retrieve engineering metrics
 * such as Change Failure Rate and Mean Time to Recovery.
 *
 *
 */
@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Metrics", description = "Engineering metrics calculation endpoints")
public class MetricsController {

    private final MetricsCalculationService metricsCalculationService;
    private final PagerDutyService pagerDutyService;
    private final GitHubActionsService gitHubActionsService;

    @GetMapping("/changeFailureRate")
    @Operation(summary = "Calculate Change Failure Rate",
            description = "Calculates the Change Failure Rate (CFR) for a specified time period")
    public ResponseEntity<ChangeFailureRateResponse> getChangeFailureRate(
            @Parameter(description = "Start date for calculation (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "End date for calculation (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("Calculating CFR for period {} to {}", startDate, endDate);


        // Calculate metric
        ChangeFailureRateResponse response = metricsCalculationService
                .calculateChangeFailureRate(startDate, endDate);


        return ResponseEntity.ok(response);
    }

    @GetMapping("/mttr")
    @Operation(summary = "Calculate Mean Time to Recovery",
            description = "Calculates the Mean Time to Recovery (MTTR) for a specified time period")
    public ResponseEntity<MTTRResponse> getMeanTimeToRecovery(
            @Parameter(description = "Start date for calculation (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "End date for calculation (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate

          ) {

        log.info("Calculating MTTR for period {} to {}", startDate, endDate);


        // Calculate metric
        MTTRResponse response = metricsCalculationService
                .calculateMeanTimeToRecovery(startDate, endDate);



        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get Dashboard Data",
            description = "Returns comprehensive dashboard data including both CFR and MTTR metrics")
    public ResponseEntity<DashboardResponse> getDashboardData(
            @Parameter(description = "Time range (7d, 30d, 90d, or custom)")
            @RequestParam(defaultValue = "7d") String timeRange,

            @Parameter(description = "Custom start date (required if timeRange=custom)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "Custom end date (required if timeRange=custom)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate


                ) {

        log.info("Getting dashboard data for timeRange: {}", timeRange);

        // Determine date range
        LocalDateTime[] dateRange = calculateDateRange(timeRange, startDate, endDate);
        LocalDateTime calculationStart = dateRange[0];
        LocalDateTime calculationEnd = dateRange[1];

        // Get both metrics
        ChangeFailureRateResponse cfr = metricsCalculationService
                .calculateChangeFailureRate(calculationStart, calculationEnd);

        MTTRResponse mttr = metricsCalculationService
                .calculateMeanTimeToRecovery(calculationStart, calculationEnd);

        // Build dashboard response
        DashboardResponse dashboard = DashboardResponse.builder()
                .changeFailureRate(cfr)
                .meanTimeToRecovery(mttr)
                .summary(DashboardResponse.DashboardSummary.builder()
                        .timeRange(timeRange)
                        .totalIncidents((int) cfr.getTotalIncidents())
                        .totalDeployments((int) cfr.getTotalDeployments())
                        .resolvedIncidents(mttr.getTotalResolvedIncidents())
                        .build())
                .build();

        return ResponseEntity.ok(dashboard);
    }



    private LocalDateTime[] calculateDateRange(String timeRange, LocalDateTime customStart, LocalDateTime customEnd) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start;

        switch (timeRange.toLowerCase()) {
            case "7d" -> start = end.minusDays(7);
            case "30d" -> start = end.minusDays(30);
            case "90d" -> start = end.minusDays(90);
            case "custom" -> {
                if (customStart == null || customEnd == null) {
                    throw new IllegalArgumentException("Custom start and end dates are required for custom time range");
                }
                start = customStart;
                end = customEnd;
            }
            default -> {
                log.warn("Unknown time range: {}, defaulting to 7d", timeRange);
                start = end.minusDays(7);
            }
        }

        return new LocalDateTime[]{start, end};
    }


    @GetMapping("/incidents")
    @Operation(summary = "Sync Incidents Data",
            description = "Calls the PagerDuty service to sync incidents data")
    public void syncIncidents(){
        pagerDutyService.syncIncidents();

    }

    @GetMapping("/deployments")
    @Operation(summary = "Sync Deployments Data",
            description = "Calls the GitHub service to sync deployments data")
    public void syncDeployments(){
        gitHubActionsService.syncDeployments();

    }
}


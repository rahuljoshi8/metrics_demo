package com.metrics.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for dashboard endpoint that provides
 * a comprehensive view of both metrics and recent activity.
 *
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    /**
     * Change Failure Rate metric data.
     */
    private ChangeFailureRateResponse changeFailureRate;

    /**
     * MeanTime to Recovery metric data.
     */
    private MTTRResponse meanTimeToRecovery;

    /**
     * Summary information about the dashboard data.
     */
    private DashboardSummary summary;

    /**
     * Nested class for incident summary information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IncidentSummary {
        private String incidentId;
        private String title;
        private String status;
        private String serviceName;
        private String createdAt;
        private String resolvedAt;
        private Long recoveryTimeMinutes;
    }

    /**
     * Nested class for deployment summary information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeploymentSummary {
        private String deploymentId;
        private String status;
        private String applicationName;
        private String version;
        private String timestamp;
    }

    /**
     * Nested class for dashboard summary information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardSummary {
        private String timeRange;
        private int totalIncidents;
        private int totalDeployments;
        private int resolvedIncidents;
    }
}
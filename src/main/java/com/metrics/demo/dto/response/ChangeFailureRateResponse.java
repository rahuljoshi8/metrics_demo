package com.metrics.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Change Failure Rate metric.
 *
 * Contains the calculated CFR along with supporting data
 * such as total deployments and incidents count.
 *
 * @author Technical Lead Assignment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeFailureRateResponse {

    /**
     * Calculated Change Failure Rate as a percentage (0.0 to 100.0).
     * Formula: (Number of incidents / Number of deployments) * 100
     */
    private double changeFailureRatePercentage;

    /**
     * Total number of deployments in the specified time range.
     */
    private long totalDeployments;

    /**
     * Total number of incidents in the specified time range.
     */
    private long totalIncidents;

    /**
     * Start date of the calculation period.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    /**
     * End date of the calculation period.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    /**
     * Time range identifier (e.g., "7d", "30d", "custom").
     */
    private String timeRange;

//    /**
//     * Optional environment filter applied.
//     */
//    private String environment;

    /**
     * Timestamp when this metric was calculated.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime calculatedAt;
}
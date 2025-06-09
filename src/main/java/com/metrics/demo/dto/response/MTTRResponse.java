package com.metrics.demo.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Mean Time to Recovery (MTTR) metric.
 *
 * Contains the calculated MTTR in multiple time units
 * along with supporting statistics.
 *
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MTTRResponse {

    /**
     * MeanTime to Recovery in minutes.
     */
    private double meanTimeToRecoveryMinutes;

    /**
     * MeanTime to Recovery in hours (for easier reading).
     */
    private double meanTimeToRecoveryHours;

    /**
     * Total number of resolved incidents used in the calculation.
     */
    private int totalResolvedIncidents;

    /**
     * Number of incidents still unresolved in the time range.
     */
    private int unresolvedIncidents;

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



    /**
     * Timestamp when this metric was calculated.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime calculatedAt;
}
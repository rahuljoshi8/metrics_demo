package com.metrics.demo.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for metrics calculation endpoints.
 *
 * Contains the time range parameters needed for calculating
 * metrics within a specific period.
 *
 * @author Technical Lead Assignment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricsRequest {

    /**
     * Start date for the metrics calculation period (inclusive).
     */
    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    /**
     * End date for the metrics calculation period (inclusive).
     */
    @NotNull(message = "End date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

//    /**
//     * Optional environment filter (e.g., "production", "staging").
//     * If not provided, all environments will be included.
//     */
//    private String environment;

    /**
     * Optional service name filter.
     * If not provided, all services will be included.
     */
    private String serviceName;
}
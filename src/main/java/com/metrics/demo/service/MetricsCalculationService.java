package com.metrics.demo.service;


import com.metrics.demo.dto.response.ChangeFailureRateResponse;
import com.metrics.demo.dto.response.MTTRResponse;

import java.time.LocalDateTime;

/**
 * Service interface for calculating engineering metrics.
 *
 * Provides methods to calculate key metrics such as Change Failure Rate
 * and Mean Time to Recovery based on incident and deployment data.
 *
 *
 */
public interface MetricsCalculationService {

    /**
     * Calculates the Change Failure Rate for a given time period.
     *
     * Change Failure Rate = (Number of incidents / Number of deployments) * 100
     *
     * @param startDate start of the calculation period (inclusive)
     * @param endDate end of the calculation period (inclusive)
     * @return ChangeFailureRateResponse containing the calculated metric
     */
    ChangeFailureRateResponse calculateChangeFailureRate(LocalDateTime startDate,
                                                         LocalDateTime endDate);

    /**
     * Calculates the Mean Time to Recovery for a given time period.
     *
     * MTTR = Total recovery time of resolved incidents / Number of resolved incidents
     *
     * @param startDate start of the calculation period (inclusive)
     * @param endDate end of the calculation period (inclusive)
     * @return MTTRResponse containing the calculated metric
     */
    MTTRResponse calculateMeanTimeToRecovery(LocalDateTime startDate,
                                             LocalDateTime endDate);

    /**
     * Determines the time range identifier based on the date range.
     *
     * @param startDate start of the period
     * @param endDate end of the period
     * @return time range identifier (e.g., "7d", "30d", "custom")
     */
    String determineTimeRange(LocalDateTime startDate, LocalDateTime endDate);
}
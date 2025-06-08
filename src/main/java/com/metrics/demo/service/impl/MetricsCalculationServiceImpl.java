package com.metrics.demo.service.impl;


import com.metrics.demo.dto.response.ChangeFailureRateResponse;
import com.metrics.demo.dto.response.MTTRResponse;
import com.metrics.demo.entity.Deployment;
import com.metrics.demo.entity.Incident;
import com.metrics.demo.repository.DeploymentRepository;
import com.metrics.demo.repository.IncidentRepository;
import com.metrics.demo.service.MetricsCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;



/**
 * Implementation of MetricsCalculationService.
 *
 * Calculates engineering metrics based on incident and deployment data
 * stored in the database.
 *
 * @author Technical Lead Assignment
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsCalculationServiceImpl implements MetricsCalculationService {

    private final IncidentRepository incidentRepository;
    private final DeploymentRepository deploymentRepository;

    @Override
    public ChangeFailureRateResponse calculateChangeFailureRate(LocalDateTime startDate,
                                                                LocalDateTime endDate) {
        log.info("Calculating Change Failure Rate for period {} to {}",
                startDate, endDate);

        // Count total deployments in the period
        long totalDeployments = deploymentRepository.countByTimestampBetween(startDate, endDate);

        // Count total incidents in the period
        long totalIncidents = incidentRepository.countByCreatedAtBetween(startDate, endDate);

        // Calculate CFR as percentage
        double cfrPercentage = totalDeployments > 0 ?
                (double) totalIncidents / totalDeployments * 100.0 : 0.0;

        String timeRange = determineTimeRange(startDate, endDate);

        log.info("CFR calculation complete: {}% ({} incidents / {} deployments)",
                String.format("%.2f", cfrPercentage), totalIncidents, totalDeployments);

        return ChangeFailureRateResponse.builder()
                .changeFailureRatePercentage(cfrPercentage)
                .totalDeployments(totalDeployments)
                .totalIncidents(totalIncidents)
                .startDate(startDate)
                .endDate(endDate)
                .timeRange(timeRange)
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public MTTRResponse calculateMeanTimeToRecovery(LocalDateTime startDate,
                                                    LocalDateTime endDate) {
        log.info("Calculating MTTR for period {} to {}}",
                startDate, endDate);

        // Get all resolved incidents in the period
        List<Incident> resolvedIncidents = incidentRepository
                .findResolvedIncidentsBetween(startDate, endDate);

        // Count unresolved incidents
        int unresolvedCount = (int) (incidentRepository.countByCreatedAtBetween(startDate, endDate)
                - resolvedIncidents.size());

        double mttrMinutes = 0.0;
        double mttrHours = 0.0;

        if (!resolvedIncidents.isEmpty()) {
            // Calculate total recovery time in minutes using Java 8 Duration
            long totalRecoveryTimeMinutes = resolvedIncidents.stream()
                    .filter(incident -> incident.getResolvedAt() != null && incident.getCreatedAt() != null)
                    .mapToLong(incident -> Duration.between(incident.getCreatedAt(), incident.getResolvedAt()).toMinutes())
                    .sum();

            // Calculate average
            mttrMinutes = (double) totalRecoveryTimeMinutes / resolvedIncidents.size();
            mttrHours = mttrMinutes / 60.0;
        }

        String timeRange = determineTimeRange(startDate, endDate);

        log.info("MTTR calculation complete: {:.2f} minutes ({:.2f} hours) for {} resolved incidents",
                mttrMinutes, mttrHours, resolvedIncidents.size());

        return MTTRResponse.builder()
                .meanTimeToRecoveryMinutes(mttrMinutes)
                .meanTimeToRecoveryHours(mttrHours)
                .totalResolvedIncidents(resolvedIncidents.size())
                .unresolvedIncidents(unresolvedCount)
                .startDate(startDate)
                .endDate(endDate)
                .timeRange(timeRange)
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public String determineTimeRange(LocalDateTime startDate, LocalDateTime endDate) {
        Duration duration = Duration.between(startDate, endDate);
        long days = duration.toDays();

        if (days <= 7) {
            return "7d";
        } else if (days <= 30) {
            return "30d";
        } else if (days <= 90) {
            return "90d";
        } else {
            return "custom";
        }
    }
}

package com.metrics.demo.service;

import com.metrics.demo.dto.response.ChangeFailureRateResponse;
import com.metrics.demo.dto.response.MTTRResponse;
import com.metrics.demo.entity.Incident;
import com.metrics.demo.enums.IncidentStatus;
import com.metrics.demo.repository.DeploymentRepository;
import com.metrics.demo.repository.IncidentRepository;
import com.metrics.demo.service.impl.MetricsCalculationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricsCalculationServiceTest {
    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private DeploymentRepository deploymentRepository;

    @InjectMocks

    private MetricsCalculationServiceImpl metricsCalculationService;

    @Test
    void calculateChangeFailureRate_ShouldCalculateCorrectly() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        when(incidentRepository.countByCreatedAtBetween(startDate, endDate)).thenReturn(2L);
        when(deploymentRepository.countByTimestampBetween(startDate, endDate)).thenReturn(10L);

        // When
        ChangeFailureRateResponse response = metricsCalculationService.calculateChangeFailureRate(startDate, endDate);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getChangeFailureRatePercentage()).isEqualTo(20.0);
    }

    @Test
    void calculateMTTR_ShouldCalculateCorrectly() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<Incident> incidents = Arrays.asList(
                createMockIncident(Duration.ofHours(2)),
                createMockIncident(Duration.ofHours(4))
        );

        when(incidentRepository.findResolvedIncidentsBetween(startDate, endDate))
                .thenReturn(incidents);

        // When
        MTTRResponse response = metricsCalculationService.calculateMeanTimeToRecovery(startDate, endDate);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMeanTimeToRecoveryHours()).isEqualTo(3);
    }

    private Incident createMockIncident(Duration recoveryTime) {
        LocalDateTime createdAt = LocalDateTime.now().minus(recoveryTime);
        LocalDateTime resolvedAt = LocalDateTime.now();
        return Incident.builder()
                .createdAt(createdAt)
                .resolvedAt(resolvedAt)
                .status(IncidentStatus.RESOLVED)
                .build();
    }
}
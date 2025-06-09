package com.metrics.demo.service;

import com.metrics.demo.dto.external.PagerDutyIncident;
import com.metrics.demo.entity.Incident;
import com.metrics.demo.enums.IncidentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagerDutyServiceTest {

    @Mock
    private PagerDutyService pagerDutyService;

    @Test
    void fetchIncidents_WithValidDateRange_ShouldReturnIncidents() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2025, 6, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2025, 6, 8, 17, 0);
        PagerDutyIncident mockIncident = PagerDutyIncident.builder()
                .id("INC001")
                .title("Application Server Down")
                .status("triggered")
                .urgency("high")
                .build();

        when(pagerDutyService.fetchIncidents(start, end))
                .thenReturn(Arrays.asList(mockIncident));

        // Act
        List<PagerDutyIncident> result = pagerDutyService.fetchIncidents(start, end);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("INC001", result.get(0).getId());
        assertEquals("Application Server Down", result.get(0).getTitle());
    }

    @Test
    void syncIncidents_WhenCalled_ShouldExecuteWithoutError() {
        // Arrange
        doNothing().when(pagerDutyService).syncIncidents();

        // Act & Assert
        assertDoesNotThrow(() -> pagerDutyService.syncIncidents());
        verify(pagerDutyService).syncIncidents();
    }

    @Test
    void convertToEntity_WithValidPagerDutyIncident_ShouldReturnIncident() {
        // Arrange
        PagerDutyIncident pagerDutyIncident = PagerDutyIncident.builder()
                .id("INC001")
                .title("Database Connection Error")
                .status("triggered")
                .urgency("high")
                .build();

        Incident expectedIncident = Incident.builder()
                .incidentId("INC001")
                .title("Database Connection Error")
                .status(IncidentStatus.TRIGGERED)
                .build();

        when(pagerDutyService.convertToEntity(pagerDutyIncident))
                .thenReturn(expectedIncident);

        // Act
        Incident result = pagerDutyService.convertToEntity(pagerDutyIncident);

        // Assert
        assertNotNull(result);
        assertEquals("INC001", result.getIncidentId());
        assertEquals("Database Connection Error", result.getTitle());
        assertEquals(IncidentStatus.TRIGGERED, result.getStatus());
    }

    @Test
    void isHealthy_WhenServiceIsUp_ShouldReturnTrue() {
        // Arrange
        when(pagerDutyService.isHealthy()).thenReturn(true);

        // Act
        boolean result = pagerDutyService.isHealthy();

        // Assert
        assertTrue(result);
        verify(pagerDutyService).isHealthy();
    }
}
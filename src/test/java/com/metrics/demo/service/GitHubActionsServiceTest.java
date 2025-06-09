package com.metrics.demo.service;

import com.metrics.demo.dto.external.GitHubWorkflowRun;
import com.metrics.demo.entity.Deployment;
import com.metrics.demo.enums.DeploymentStatus;
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
class GitHubActionsServiceTest {

    @Mock
    private GitHubActionsService gitHubActionsService;

    @Test
    void fetchWorkflowRuns_WithValidDateRange_ShouldReturnWorkflowRuns() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2025, 6, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 6, 8, 18, 0);
        GitHubWorkflowRun mockRun = GitHubWorkflowRun.builder()
                .id(123L)
                .name("Release Pipeline")
                .status("completed")
                .conclusion("success")
                .build();

        when(gitHubActionsService.fetchWorkflowRuns(start, end))
                .thenReturn(Arrays.asList(mockRun));

        // Act
        List<GitHubWorkflowRun> result = gitHubActionsService.fetchWorkflowRuns(start, end);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Release Pipeline", result.get(0).getName());
    }

    @Test
    void syncDeployments_WhenCalled_ShouldExecuteWithoutError() {
        // Arrange
        doNothing().when(gitHubActionsService).syncDeployments();

        // Act & Assert
        assertDoesNotThrow(() -> gitHubActionsService.syncDeployments());
        verify(gitHubActionsService).syncDeployments();
    }

    @Test
    void convertToEntity_WithValidWorkflowRun_ShouldReturnDeployment() {
        // Arrange
        GitHubWorkflowRun workflowRun = GitHubWorkflowRun.builder()
                .id(123L)
                .name("Release Pipeline")
                .status("completed")
                .conclusion("success")
                .build();

        Deployment expectedDeployment = Deployment.builder()
                .deploymentId("123")
                .status(DeploymentStatus.SUCCESS)
                .build();

        when(gitHubActionsService.convertToEntity(workflowRun))
                .thenReturn(expectedDeployment);

        // Act
        Deployment result = gitHubActionsService.convertToEntity(workflowRun);

        // Assert
        assertNotNull(result);
        assertEquals("123", result.getDeploymentId());
        assertEquals(DeploymentStatus.SUCCESS, result.getStatus());
    }

    @Test
    void isHealthy_WhenServiceIsUp_ShouldReturnTrue() {
        // Arrange
        when(gitHubActionsService.isHealthy()).thenReturn(true);

        // Act
        boolean result = gitHubActionsService.isHealthy();

        // Assert
        assertTrue(result);
        verify(gitHubActionsService).isHealthy();
    }
}
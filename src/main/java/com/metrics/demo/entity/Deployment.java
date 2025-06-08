package com.metrics.demo.entity;

import com.metrics.demo.enums.DeploymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a deployment from GitHub Actions.
 *
 * This entity stores deployment information used for calculating metrics such as
 * Change Failure Rate. Each deployment represents a change to the system that
 * could potentially cause incidents.
 *
 * Key fields for metrics calculation:
 * - timestamp: When the deployment occurred (used for time-range filtering)
 * - status: Whether the deployment was successful or failed
 *
 * @author Technical Lead Assignment
 */
@Entity
@Table(name = "deployments", indexes = {
        @Index(name = "idx_deployment_timestamp", columnList = "timestamp"),
        @Index(name = "idx_deployment_status", columnList = "status")})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deployment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique identifier from GitHub Actions (typically workflow run ID)
     */
    @Column(name = "deployment_id", unique = true, nullable = false)
    @NotNull
    @Size(max = 255)
    private String deploymentId;

    /**
     * When the deployment occurred
     * Critical for determining deployment count in time ranges for CFR calculation
     */
    @Column(name = "timestamp", nullable = false)
    @NotNull
    private LocalDateTime timestamp;

    /**
     * Status of the deployment (SUCCESS, FAILURE, CANCELLED)
     * Used to determine if deployment was successful
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull
    private DeploymentStatus status;

//    /**
//     * Environment where deployment occurred (e.g., production, staging)
//     */
//    @Column(name = "environment", length = 100)
//    @Size(max = 100)
//    private String environment;

    /**
     * Name of the application being deployed
     */
    @Column(name = "application_name")
    @Size(max = 255)
    private String applicationName;

    /**
     * Version or commit hash being deployed
     */
    @Column(name = "version", length = 100)
    @Size(max = 100)
    private String version;

    /**
     * GitHub Actions workflow run ID for reference
     */
    @Column(name = "workflow_run_id")
    private Long workflowRunId;

    /**
     * GitHub repository where the deployment originated
     */
    @Column(name = "repository_name")
    @Size(max = 255)
    private String repositoryName;

    /**
     * GitHub workflow name
     */
    @Column(name = "workflow_name")
    @Size(max = 255)
    private String workflowName;

    /**
     * Record creation timestamp (for auditing)
     */
    @CreationTimestamp
    @Column(name = "record_created_at", updatable = false)
    private LocalDateTime recordCreatedAt;

    /**
     * Record last update timestamp (for auditing)
     */
    @UpdateTimestamp
    @Column(name = "record_updated_at")
    private LocalDateTime recordUpdatedAt;

    /**
     * Checks if this deployment was successful.
     *
     * @return true if deployment status is SUCCESS, false otherwise
     */
    public boolean isSuccessful() {
        return DeploymentStatus.SUCCESS.equals(status);
    }

    /**
     * Checks if this deployment failed.
     *
     * @return true if deployment status is FAILURE, false otherwise
     */
    public boolean isFailed() {
        return DeploymentStatus.FAILURE.equals(status);
    }
}
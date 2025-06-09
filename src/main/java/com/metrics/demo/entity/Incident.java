package com.metrics.demo.entity;

import com.metrics.demo.enums.IncidentStatus;
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
 * Entity representing an incident from PagerDuty.
 *
 * This entity stores incident information used for calculating metrics such as
 * Change Failure Rate and Mean Time to Recovery (MTTR).
 *
 * Key fields for metrics calculation:
 * - createdAt: When the incident was first triggered
 * - acknowledgedAt: When someone acknowledged the incident
 * - resolvedAt: When the incident was resolved (used for MTTR calculation)
 *
 *
 */
@Entity
@Table(name = "incidents", indexes = {
        @Index(name = "idx_incident_created_at", columnList = "created_at"),
        @Index(name = "idx_incident_resolved_at", columnList = "resolved_at"),
        @Index(name = "idx_incident_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique identifier from PagerDuty API
     */
    @Column(name = "incident_id", unique = true, nullable = false)
    @NotNull
    @Size(max = 255)
    private String incidentId;

    /**
     * Human-readable title of the incident
     */
    @Column(name = "title", length = 500)
    @Size(max = 500)
    private String title;

    /**
     * Current status of the incident (TRIGGERED, ACKNOWLEDGED, RESOLVED)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull
    private IncidentStatus status;

    /**
     * Urgency level (HIGH, LOW)
     */
    @Column(name = "urgency", length = 50)
    @Size(max = 50)
    private String urgency;

    /**
     * Name of the service that triggered the incident
     */
    @Column(name = "service_name")
    @Size(max = 255)
    private String serviceName;

    /**
     * When the incident was first created/triggered
     * Critical for determining incident count in time ranges
     */
    @Column(name = "created_at", nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    /**
     * When the incident was acknowledged by someone
     * Used for response time metrics
     */
    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    /**
     * When the incident was resolved
     * Critical for MTTR calculation: resolvedAt - createdAt = recovery time
     */
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    /**
     * Original PagerDuty incident key for reference
     */
    @Column(name = "pagerduty_incident_key")
    @Size(max = 255)
    private String pagerdutyIncidentKey;

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
     * Calculates the recovery time in minutes for resolved incidents.
     *
     * @return recovery time in minutes, or null if incident is not resolved
     */
    public Long getRecoveryTimeMinutes() {
        if (resolvedAt == null || createdAt == null) {
            return null;
        }
        return java.time.Duration.between(createdAt, resolvedAt).toMinutes();
    }

    /**
     * Checks if this incident is resolved (has a resolved timestamp).
     *
     * @return true if incident is resolved, false otherwise
     */
    public boolean isResolved() {
        return resolvedAt != null;
    }
}
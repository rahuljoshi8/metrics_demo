package com.metrics.demo.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for PagerDuty incident data from API responses.
 *
 * Maps to the structure returned by PagerDuty's REST API
 * for incident objects.
 *
 * @author Technical Lead Assignment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagerDutyIncident {

    /**
     * Unique incident identifier from PagerDuty.
     */
    @JsonProperty("id")
    private String id;

    /**
     * Incident number (human-readable identifier).
     */
    @JsonProperty("incident_number")
    private Integer incidentNumber;

    /**
     * Brief description of the incident.
     */
    @JsonProperty("title")
    private String title;

    /**
     * Current status of the incident.
     */
    @JsonProperty("status")
    private String status;

    /**
     * Incident key for grouping related alerts.
     */
    @JsonProperty("incident_key")
    private String incidentKey;

    /**
     * Service information.
     */
    @JsonProperty("service")
    private PagerDutyService service;

    /**
     * Urgency level of the incident.
     */
    @JsonProperty("urgency")
    private String urgency;

    /**
     * When the incident was created.
     */
    @JsonProperty("created_at")
    private String createdAt;

    /**
     * When the incident was last updated.
     */
    @JsonProperty("updated_at")
    private String updatedAt;

    /**
     * First acknowledgment time.
     */
    @JsonProperty("first_trigger_log_entry")
    private LogEntry firstTriggerLogEntry;

    /**
     * Last status change log entry.
     */
    @JsonProperty("last_status_change_log_entry")
    private LogEntry lastStatusChangeLogEntry;

    @JsonProperty("acknowledged_at")
    private String acknowledgedAt;

    @JsonProperty("resolved_at")
    private String resolvedAt;

    /**
     * Nested class for PagerDuty service information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PagerDutyService {
        @JsonProperty("id")
        private String id;

        @JsonProperty("summary")
        private String summary;
    }

    /**
     * Nested class for log entry information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LogEntry {
        @JsonProperty("id")
        private String id;

        @JsonProperty("type")
        private String type;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("agent")
        private Agent agent;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Agent {
            @JsonProperty("id")
            private String id;

            @JsonProperty("type")
            private String type;
        }
    }
}
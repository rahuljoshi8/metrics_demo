package com.metrics.demo.enums;

/**
 * Enumeration of possible incident statuses in PagerDuty.
 *
 * These statuses represent the lifecycle of an incident:
 * - TRIGGERED: Incident has been created but not yet acknowledged
 * - ACKNOWLEDGED: Someone has acknowledged the incident and is working on it
 * - RESOLVED: Incident has been resolved and is closed
 *
 */
public enum IncidentStatus {

    /**
     * Incident has been triggered but not yet acknowledged.
     * This is the initial state when an incident is created.
     */
    TRIGGERED,

    /**
     * Incident has been acknowledged by a responder.
     * This indicates someone is actively working on the incident.
     */
    ACKNOWLEDGED,

    /**
     * Incident has been resolved and is closed.
     * This is the final state - used for MTTR calculations.
     */
    RESOLVED,

    UNKNOWN

}

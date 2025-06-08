package com.metrics.demo.service;


import com.metrics.demo.dto.external.PagerDutyIncident;
import com.metrics.demo.entity.Incident;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for PagerDuty integration.
 *
 * Provides methods to fetch incident data from PagerDuty API
 * and synchronize it with the local database.
 *
 * @author Technical Lead Assignment
 */
public interface PagerDutyService {

    /**
     * Fetches incidents from PagerDuty API within a date range.
     *
     * @param since start date for incident retrieval
     * @param until end date for incident retrieval
     * @return list of incidents from PagerDuty API
     */
    List<PagerDutyIncident> fetchIncidents(LocalDateTime since, LocalDateTime until);

    /**
     * Synchronizes incidents from PagerDuty to local database.
     * This method fetches recent incidents and updates the database.
     */
    void syncIncidents();

    /**
     * Converts PagerDuty incident DTO to internal entity.
     *
     * @param pagerDutyIncident the external DTO
     * @return converted internal entity
     */
    Incident convertToEntity(PagerDutyIncident pagerDutyIncident);

    /**
     * Fetches a specific incident by ID from PagerDuty.
     *
     * @param incidentId the PagerDuty incident ID
     * @return the incident data, or null if not found
     */
    PagerDutyIncident fetchIncidentById(String incidentId);

    /**
     * Checks the health of PagerDuty API connection.
     *
     * @return true if API is accessible, false otherwise
     */
    boolean isHealthy();
}
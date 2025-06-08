package com.metrics.demo.service.impl;


import com.metrics.demo.dto.external.PagerDutyIncident;
import com.metrics.demo.entity.Incident;
import com.metrics.demo.enums.IncidentStatus;
import com.metrics.demo.repository.IncidentRepository;
import com.metrics.demo.service.PagerDutyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of PagerDutyService.
 *
 * Handles integration with PagerDuty API to fetch incident data
 * and synchronize it with the local database.
 *
 * @author Technical Lead Assignment
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PagerDutyServiceImpl implements PagerDutyService {

    private final IncidentRepository incidentRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${pagerduty.api.token}")
    private String apiToken;

    @Value("${pagerduty.api.url}")
    private String baseUrl;

    @Value("${pagerduty.api.rate-limit:120}")
    private int rateLimit;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public List<PagerDutyIncident> fetchIncidents(LocalDateTime since, LocalDateTime until) {
        log.info("Fetching PagerDuty incidents from {} to {}", since, until);

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(baseUrl)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Token token=" + apiToken)
                    .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.pagerduty+json;version=2")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            String sinceParam = since.format(ISO_FORMATTER) + "Z";
            String untilParam = until.format(ISO_FORMATTER) + "Z";

            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/incidents")
                            .queryParam("since", sinceParam)
                            .queryParam("until", untilParam)
                            .queryParam("limit", 100)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> incidents = (List<Map<String, Object>>) response.get("incidents");

            // Convert to DTOs (simplified conversion)
            List<PagerDutyIncident> result = new ArrayList<>();
            if (incidents != null) {
                for (Map<String, Object> incidentData : incidents) {
                    result.add(convertMapToIncident(incidentData));
                }
            }

            log.info("Fetched {} incidents from PagerDuty", result.size());
            return result;

        } catch (WebClientResponseException e) {
            log.error("Error fetching incidents from PagerDuty: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to fetch incidents from PagerDuty", e);
        } catch (Exception e) {
            log.error("Unexpected error fetching incidents from PagerDuty", e);
            throw new RuntimeException("Failed to fetch incidents from PagerDuty", e);
        }
    }

    @Override
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @Transactional
    public void syncIncidents() {
        log.info("Starting scheduled incident synchronization");

        try {
            // Fetch incidents from the last 24 hours
            LocalDateTime until = LocalDateTime.now();
            LocalDateTime since = until.minusDays(24);

            List<PagerDutyIncident> incidents = fetchIncidents(since, until);

            for (PagerDutyIncident pdIncident : incidents) {
                syncIncident(pdIncident);
            }

            log.info("Completed incident synchronization, processed {} incidents", incidents.size());

        } catch (Exception e) {
            log.error("Error during scheduled incident synchronization", e);
        }
    }

    @Override
    public Incident convertToEntity(PagerDutyIncident pagerDutyIncident) {
        return Incident.builder()
                .incidentId(pagerDutyIncident.getId())
                .title(pagerDutyIncident.getTitle())
                .status(parseIncidentStatus(pagerDutyIncident.getStatus()))
                .urgency(pagerDutyIncident.getUrgency())
                .serviceName(pagerDutyIncident.getService() != null ?
                        pagerDutyIncident.getService().getSummary() : null)
                .createdAt(parseDateTime(pagerDutyIncident.getCreatedAt()))
                .acknowledgedAt(parseDateTime(pagerDutyIncident.getAcknowledgedAt()))
                .resolvedAt(parseDateTime(pagerDutyIncident.getResolvedAt()))
                .pagerdutyIncidentKey(pagerDutyIncident.getIncidentKey())
                .build();
    }

    @Override
    public PagerDutyIncident fetchIncidentById(String incidentId) {
        log.debug("Fetching PagerDuty incident by ID: {}", incidentId);

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(baseUrl)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Token token=" + apiToken)
                    .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.pagerduty+json;version=2")
                    .build();

            Map<String, Object> response = webClient.get()
                    .uri("/incidents/{id}", incidentId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            @SuppressWarnings("unchecked")
            Map<String, Object> incidentData = (Map<String, Object>) response.get("incident");

            return incidentData != null ? convertMapToIncident(incidentData) : null;

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Incident not found: {}", incidentId);
                return null;
            }
            log.error("Error fetching incident {} from PagerDuty: {}", incidentId, e.getMessage());
            throw new RuntimeException("Failed to fetch incident from PagerDuty", e);
        }
    }

    @Override
    public boolean isHealthy() {
        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(baseUrl)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Token token=" + apiToken)
                    .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.pagerduty+json;version=2")
                    .build();

            webClient.get()
                    .uri("/incidents?limit=1")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return true;
        } catch (Exception e) {
            log.warn("PagerDuty health check failed", e);
            return false;
        }
    }

    private void syncIncident(PagerDutyIncident pdIncident) {
        Optional<Incident> existing = incidentRepository.findByIncidentId(pdIncident.getId());

        if (existing.isPresent()) {
            // Update existing incident
            Incident incident = existing.get();
            incident.setStatus(parseIncidentStatus(pdIncident.getStatus()));
            incident.setTitle(pdIncident.getTitle());
            // Update other fields as needed

            incidentRepository.save(incident);
            log.debug("Updated existing incident: {}", incident.getIncidentId());
        } else {
            // Create new incident
            Incident newIncident = convertToEntity(pdIncident);
            incidentRepository.save(newIncident);
            log.debug("Created new incident: {}", newIncident.getIncidentId());
        }
    }

    private PagerDutyIncident convertMapToIncident(Map<String, Object> incidentData) {
        // Simplified conversion - you may need to enhance this based on actual API response structure
        return PagerDutyIncident.builder()
                .id((String) incidentData.get("id"))
                .title((String) incidentData.get("title"))
                .status((String) incidentData.get("status"))
                .incidentKey((String) incidentData.get("incident_key"))
                .urgency((String) incidentData.get("urgency"))
                .createdAt((String) incidentData.get("created_at"))
                .updatedAt((String) incidentData.get("updated_at"))
                .acknowledgedAt((String) incidentData.get("acknowledged_at"))
                .resolvedAt((String) incidentData.get("resolved_at"))
                .service(convertMapToService((Map<String, Object>) incidentData.get("service")))
                .build();
    }

    private PagerDutyIncident.PagerDutyService convertMapToService(Map<String, Object> serviceData) {
        if (serviceData == null) return null;
        return PagerDutyIncident.PagerDutyService.builder()
                .id((String) serviceData.get("id"))
                .summary((String) serviceData.get("summary"))
                .build();
    }
    private IncidentStatus parseIncidentStatus(String status) {
        if (status == null) return IncidentStatus.TRIGGERED;

        return switch (status.toLowerCase()) {
            case "acknowledged" -> IncidentStatus.ACKNOWLEDGED;
            case "resolved" -> IncidentStatus.RESOLVED;
            default -> IncidentStatus.TRIGGERED;
        };
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null) return LocalDateTime.now();

        try {
            // Remove 'Z' suffix and parse
            String cleanDateTime = dateTimeString.replace("Z", "");
            return LocalDateTime.parse(cleanDateTime, ISO_FORMATTER);
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}, using current time", dateTimeString);
            return LocalDateTime.now();
        }
    }
}
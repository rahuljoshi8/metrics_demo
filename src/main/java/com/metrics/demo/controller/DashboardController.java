package com.metrics.demo.controller;


import com.metrics.demo.dto.response.DashboardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Web controller for the DORA metrics dashboard UI.
 * Serves Thymeleaf templates and handles form submissions.
 */
@Slf4j
@Controller
@RequestMapping("/")
public class DashboardController {



    @Autowired
    private MetricsController metricsController;

    /**
     * Main dashboard page
     */
    @GetMapping
    public String dashboard(@RequestParam(defaultValue = "7d") String timeRange,
                            @RequestParam(required = false) String startDate,
                            @RequestParam(required = false) String endDate,
                            Model model) {

        log.info("Loading dashboard with timeRange: {}, startDate: {}, endDate: {}",
                timeRange, startDate, endDate);

        try {
            LocalDateTime start = null;
            LocalDateTime end = null;

            if ("custom".equals(timeRange)) {
                if (startDate != null && endDate != null) {
                    start = LocalDateTime.parse(startDate);
                    end = LocalDateTime.parse(endDate);
                } else {
                    throw new IllegalArgumentException("Start date and end date are required for custom range");
                }
            }

            // Get the ResponseEntity and extract the body
            ResponseEntity<DashboardResponse> response = metricsController.getDashboardData(timeRange, start, end);
            DashboardResponse dashboardData = response.getBody();

            model.addAttribute("dashboardData", dashboardData);
            model.addAttribute("selectedTimeRange", timeRange);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("lastUpdated",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));

        } catch (Exception e) {
            log.error("Error loading dashboard data: {}", e.getMessage(), e);
            model.addAttribute("error", "Unable to load dashboard data: " + e.getMessage());
        }

        return "dashboard";
    }
    /**
     * Sync deployments from GitHub
     */
    @PostMapping("sync/deployments")
    public String syncDeployments(RedirectAttributes redirectAttributes) {
        log.info("Syncing deployments from GitHub Actions");

        try {
            metricsController.syncDeployments();
            redirectAttributes.addFlashAttribute("successMessage", "✅ Deployments synced successfully!");
            log.info("Deployments sync completed successfully");

        } catch (Exception e) {
            log.error("Error syncing deployments: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Failed to sync deployments: " + e.getMessage());
        }

        return "redirect:/";
    }

    /**
     * Sync incidents from PagerDuty
     */
    @PostMapping("sync/incidents")
    public String syncIncidents(RedirectAttributes redirectAttributes) {
        log.info("Syncing incidents from PagerDuty");

        try {
            metricsController.syncIncidents();
            redirectAttributes.addFlashAttribute("successMessage", "✅ Incidents synced successfully!");
            log.info("Incidents sync completed successfully");

        } catch (Exception e) {
            log.error("Error syncing incidents: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Failed to sync incidents: " + e.getMessage());
        }

        return "redirect:/";
    }

}
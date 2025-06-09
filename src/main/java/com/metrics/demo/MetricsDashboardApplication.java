package com.metrics.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Engineering Metrics Dashboard.
 *
 * This application provides endpoints to calculate and display key engineering metrics:
 * - Change Failure Rate (CFR): Number of incidents / Number of deployments
 * - Mean Time to Recovery (MTTR): Average time to resolve incidents
 *
 * The application integrates with PagerDuty for incident data and GitHub Actions for deployment data.
 *
 */
@SpringBootApplication
@EnableScheduling
public class MetricsDashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetricsDashboardApplication.class, args);
	}
}
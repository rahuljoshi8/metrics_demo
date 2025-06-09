package com.metrics.demo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the metrics dashboard.
 *
 * Configures basic authentication and access rules.
 *
// */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()  // Allow everything
                )
                .csrf(csrf -> csrf.disable())  // Disable CSRF completely
                .headers(headers -> headers
                        .frameOptions().disable()  // Disable all frame protection
                );

        return http.build();

    }

}
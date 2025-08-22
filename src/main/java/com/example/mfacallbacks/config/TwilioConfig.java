package com.example.mfacallbacks.config;

import com.twilio.Twilio;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuration class for Twilio SMS service integration.
 * 
 * <p>This class is responsible for initializing the Twilio client with credentials
 * from application properties. It runs at application startup and logs the initialization status.
 * 
 * <p>Required configuration properties:
 * <ul>
 *   <li>twilio.account-sid: Twilio Account SID</li>
 *   <li>twilio.auth-token: Twilio Auth Token</li>
 * </ul>
 * 
 * <p>If either of these properties is not configured, SMS functionality will be disabled
 * and a warning will be logged.
 */
@Slf4j
@Configuration
@Hidden // Hide from OpenAPI documentation
public class TwilioConfig {

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    /**
     * Initializes the Twilio client with the configured credentials.
     * This method runs automatically when the Spring application context is initialized.
     * 
     * <p>If credentials are not properly configured, logs a warning message
     * and continues without failing the application startup.
     */
    @PostConstruct
    public void initTwilio() {
        if (accountSid != null && authToken != null) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio initialized successfully");
        } else {
            log.warn("Twilio credentials not configured. SMS functionality will be disabled.");
        }
    }
}

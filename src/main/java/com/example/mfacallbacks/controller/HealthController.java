package com.example.mfacallbacks.controller;

import com.example.mfacallbacks.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @Value("${spring.application.name}")
    private String applicationName;

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHealthStatus() {
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("service", applicationName);
        healthData.put("status", "UP");
        healthData.put("timestamp", Instant.now().toString());
        healthData.put("version", "1.0.0");
        
        return ResponseEntity.ok(ApiResponse.success("Service is healthy", healthData));
    }
}

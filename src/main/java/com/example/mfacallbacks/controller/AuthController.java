package com.example.mfacallbacks.controller;

import com.example.mfacallbacks.dto.ApiResponse;
import com.example.mfacallbacks.dto.AuthRequest;
import com.example.mfacallbacks.dto.OtpVerificationRequest;
import com.example.mfacallbacks.service.OtpService;
import com.example.mfacallbacks.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "MFA Authentication endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AuthController {

    private final OtpService otpService;
    private final SmsService smsService;

    @Operation(summary = "Initiate MFA", description = "Generate and send OTP via SMS for multi-factor authentication")
    @PostMapping("/initiate-mfa")
    public ResponseEntity<ApiResponse<String>> initiateMfa(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AuthRequest request) {
        
        String userId = jwt.getSubject();
        String phoneNumber = request.getPhoneNumber();
        
        // Generate OTP
        String otp = otpService.generateOtp(userId);
        
        // Send OTP via SMS
        smsService.sendOtp(phoneNumber, otp);
        
        log.info("MFA initiated for user: {}", userId);
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully"));
    }

    @Operation(summary = "Verify OTP", description = "Validate the OTP provided by the user")
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody OtpVerificationRequest request) {
        
        String userId = jwt.getSubject();
        
        // Validate OTP
        boolean isValid = otpService.validateOtp(userId, request.getOtp());
        
        if (!isValid) {
            log.warn("Invalid OTP attempt for user: {}", userId);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid or expired OTP"));
        }
        
        log.info("OTP verified successfully for user: {}", userId);
        return ResponseEntity.ok(ApiResponse.success("OTP verified successfully"));
    }
}

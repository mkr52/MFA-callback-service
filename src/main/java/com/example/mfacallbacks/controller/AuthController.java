package com.example.mfacallbacks.controller;

import com.example.mfacallbacks.dto.ApiResponseDTO;
import com.example.mfacallbacks.dto.AuthRequest;
import com.example.mfacallbacks.dto.OtpVerificationRequest;
import com.example.mfacallbacks.service.OtpService;
import com.example.mfacallbacks.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling Multi-Factor Authentication (MFA) operations.
 * 
 * <p>This controller provides endpoints for initiating MFA and verifying OTPs.
 * All endpoints require a valid JWT token in the Authorization header.
 * 
 * <p>Base URL: /api/v1/auth
 * 
 * <p>Security:
 * <ul>
 *   <li>All endpoints require JWT authentication</li>
 *   <li>Uses Bearer token authentication</li>
 *   <li>Requires valid OAuth2 JWT with 'sub' claim</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
    name = "Authentication", 
    description = "Endpoints for Multi-Factor Authentication (MFA) operations"
)
@SecurityRequirement(name = "Bearer Authentication")
public class AuthController {

    private final OtpService otpService;
    private final SmsService smsService;

    /**
     * Initiates the MFA process by generating and sending an OTP to the user's phone number.
     * 
     * <p>This endpoint:
     * <ol>
     *   <li>Generates a new OTP for the authenticated user</li>
     *   <li>Stores the OTP with an expiration time</li>
     *   <li>Sends the OTP to the provided phone number via SMS</li>
     * </ol>
     * 
     * @param jwt The authenticated user's JWT token (automatically injected)
     * @param request The authentication request containing the user's phone number
     * @return ApiResponse with success message if OTP was sent successfully
     */
    @Operation(
        summary = "Initiate MFA", 
        description = "Generate and send OTP via SMS for multi-factor authentication",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "OTP sent successfully",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                        value = "{\"success\":true,\"message\":\"OTP sent successfully\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing JWT token",
                content = @Content
            )
        }
    )
    @PostMapping(
        value = "/initiate-mfa",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponseDTO<String>> initiateMfa(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AuthRequest request) {
        
        String userId = jwt.getSubject();
        String phoneNumber = request.getPhoneNumber();
        
        // Generate OTP with expiration time
        String otp = otpService.generateOtp(userId);
        
        // Send OTP via SMS asynchronously
        smsService.sendOtp(phoneNumber, otp);
        
        log.info("MFA initiated for user: {}", userId);
        return ResponseEntity.ok(ApiResponseDTO.success("OTP sent successfully"));
    }

    /**
     * Verifies the OTP provided by the user for MFA authentication.
     * 
     * <p>This endpoint:
     * <ol>
     *   <li>Validates the OTP against the one stored for the user</li>
     *   <li>Checks if the OTP is not expired</li>
     *   <li>Consumes the OTP after successful validation (one-time use)</li>
     * </ol>
     * 
     * @param jwt The authenticated user's JWT token (automatically injected)
     * @param request The OTP verification request containing the OTP to validate
     * @return ApiResponse with verification status
     */
    @Operation(
        summary = "Verify OTP",
        description = "Validate the OTP provided by the user for MFA authentication",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "OTP verified successfully",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                        value = "{\"success\":true,\"message\":\"OTP verified successfully\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid or expired OTP",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                        value = "{\"success\":false,\"message\":\"Invalid or expired OTP\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing JWT token",
                content = @Content
            )
        }
    )
    @PostMapping(
        value = "/verify-otp",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponseDTO<String>> verifyOtp(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody OtpVerificationRequest request) {
        
        String userId = jwt.getSubject();
        
        // Validate OTP (this also consumes it if valid)
        boolean isValid = otpService.validateOtp(userId, request.getOtp());
        
        if (!isValid) {
            log.warn("Invalid OTP attempt for user: {}", userId);
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.error("Invalid or expired OTP"));
        }
        
        log.info("OTP verified successfully for user: {}", userId);
        return ResponseEntity.ok(ApiResponseDTO.success("OTP verified successfully"));
    }
}

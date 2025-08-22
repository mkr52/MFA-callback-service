package com.example.mfacallbacks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for OTP verification request.
 * Contains the user ID and OTP to be validated.
 */
@Data
@Schema(description = "Request payload for OTP verification")
@lombok.Generated
public class OtpVerificationRequest {
    
    /**
     * The unique identifier of the user.
     * This should match the subject (sub) claim from the JWT token.
     */
    @NotBlank(message = "User ID is required")
    @Schema(
        description = "User's unique identifier (must match JWT sub claim)",
        example = "user-12345",
        required = true
    )
    private String userId;
    
    /**
     * The one-time password (OTP) to be verified.
     * Must be a numeric string with 4-8 digits.
     */
    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^\\d{4,8}$", message = "OTP must be 4-8 digits")
    @Schema(
        description = "One-time password to verify",
        example = "123456",
        minLength = 4,
        maxLength = 8,
        required = true
    )
    private String otp;
}

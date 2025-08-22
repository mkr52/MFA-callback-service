package com.example.mfacallbacks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Data Transfer Object for MFA initiation request.
 * Contains the user's phone number for OTP delivery.
 */
@Data
@Schema(description = "Request payload for initiating MFA")
@lombok.Generated
public class AuthRequest {
    
    /**
     * The user's phone number in E.164 format (e.g., +1234567890).
     * Must be a valid phone number.
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^\\+[1-9]\\d{1,14}$",
        message = "Phone number must be in E.164 format (e.g., +1234567890)"
    )
    @Schema(
        description = "User's phone number in E.164 format",
        example = "+1234567890",
        required = true
    )
    private String phoneNumber;
}

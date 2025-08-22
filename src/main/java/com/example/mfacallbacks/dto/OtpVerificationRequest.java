package com.example.mfacallbacks.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpVerificationRequest {
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "OTP is required")
    private String otp;
}

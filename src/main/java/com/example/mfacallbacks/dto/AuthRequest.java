package com.example.mfacallbacks.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}

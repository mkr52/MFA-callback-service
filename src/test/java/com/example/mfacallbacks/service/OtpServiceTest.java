package com.example.mfacallbacks.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @InjectMocks
    private OtpService otpService;

    private final String testUserId = "test-user-123";

    @Test
    void generateOtp_ShouldGenerateOtpOfSpecifiedLength() {
        // Act
        String otp = otpService.generateOtp(testUserId);
        
        // Assert
        assertNotNull(otp);
        assertEquals(6, otp.length()); // Default length is 6
        assertTrue(otp.matches("\\d{6}")); // Should be 6 digits
    }

    @Test
    void validateOtp_WithValidOtp_ShouldReturnTrue() {
        // Arrange
        String otp = otpService.generateOtp(testUserId);
        
        // Act
        boolean isValid = otpService.validateOtp(testUserId, otp);
        
        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateOtp_WithInvalidOtp_ShouldReturnFalse() {
        // Arrange
        otpService.generateOtp(testUserId);
        
        // Act
        boolean isValid = otpService.validateOtp(testUserId, "000000");
        
        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateOtp_WithExpiredOtp_ShouldReturnFalse() throws InterruptedException {
        // Arrange - Set a very short expiry time for testing
        otpService = new OtpService();
        otpService.setOtpExpirySeconds(1); // 1 second expiry
        
        String otp = otpService.generateOtp(testUserId);
        Thread.sleep(2000); // Wait for OTP to expire
        
        // Act
        boolean isValid = otpService.validateOtp(testUserId, otp);
        
        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateOtp_AfterValidation_ShouldRemoveOtp() {
        // Arrange
        String otp = otpService.generateOtp(testUserId);
        
        // Act - First validation (should be valid)
        boolean firstAttempt = otpService.validateOtp(testUserId, otp);
        // Second validation with same OTP (should be invalid as OTP is removed after first use)
        boolean secondAttempt = otpService.validateOtp(testUserId, otp);
        
        // Assert
        assertTrue(firstAttempt);
        assertFalse(secondAttempt);
    }
}

package com.example.mfacallbacks.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();

    @Value("${app.otp.length:6}")
    private int otpLength = 6;  // Default to 6 digits

    @Value("${app.otp.expiry-minutes:5}")
    private int otpExpiryMinutes = 5;
    private long otpExpirySeconds = 300; // 5 minutes in seconds
    
    // For testing purposes only
    public void setOtpExpirySeconds(long otpExpirySeconds) {
        this.otpExpirySeconds = otpExpirySeconds;
    }

    @PostConstruct
    public void init() {
        if (otpExpiryMinutes > 0) {
            this.otpExpirySeconds = otpExpiryMinutes * 60L;
        }
        log.debug("OTP Service initialized with OTP length: {}, Expiry: {} seconds", 
                 otpLength, otpExpirySeconds);
    }

    public String generateOtp(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        
        // Ensure we have a valid OTP length (at least 4 digits)
        int length = Math.max(4, otpLength);
        
        // Generate a random number with exactly 'length' digits
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(RANDOM.nextInt(10)); // Append a random digit (0-9)
        }
        
        String otpString = otp.toString();
        long expiryTime = Instant.now().plusSeconds(otpExpirySeconds).getEpochSecond();
        
        // Store the OTP
        otpStore.put(userId, new OtpData(otpString, expiryTime));
        log.debug("Generated OTP for user {}: {}", userId, otpString);
        
        return otpString;
    }

    public boolean validateOtp(String userId, String otp) {
        if (userId == null || otp == null) {
            return false;
        }
        
        OtpData otpData = otpStore.get(userId);
        if (otpData == null) {
            log.debug("No OTP found for user: {}", userId);
            return false;
        }
        
        boolean isOtpValid = otp.equals(otpData.getOtp());
        boolean isNotExpired = otpData.getExpiryTime() > Instant.now().getEpochSecond();
        
        if (isOtpValid && isNotExpired) {
            // Remove the OTP after successful validation
            otpStore.remove(userId);
            log.debug("Valid OTP for user: {}", userId);
            return true;
        }
        
        if (!isNotExpired) {
            log.debug("OTP expired for user: {}", userId);
            otpStore.remove(userId); // Clean up expired OTP
        } else if (!isOtpValid) {
            log.debug("Invalid OTP for user: {}", userId);
        }
        
        return false;
    }

    @Async
    public void clearExpiredOtps() {
        long currentTime = Instant.now().getEpochSecond();
        otpStore.entrySet().removeIf(entry -> entry.getValue().getExpiryTime() <= currentTime);
    }

    private static class OtpData {
        private final String otp;
        private final long expiryTime;

        public OtpData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public String getOtp() {
            return otp;
        }

        public long getExpiryTime() {
            return expiryTime;
        }
    }
}

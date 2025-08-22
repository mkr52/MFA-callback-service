package com.example.mfacallbacks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service responsible for OTP (One-Time Password) generation and validation.
 * 
 * <p>This service provides thread-safe OTP operations with configurable length and expiry times.
 * OTPs are stored in-memory and automatically cleaned up when they expire.
 * 
 * <p>Configuration is done through application properties:
 * <ul>
 *   <li>app.otp.length: Length of generated OTP (default: 6)</li>
 *   <li>app.otp.expiry-minutes: OTP validity period in minutes (default: 5)</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    /** Secure random number generator for OTP generation */
    private static final SecureRandom RANDOM = new SecureRandom();
    
    /** Thread-safe map to store active OTPs with user ID as key */
    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();

    /** Length of generated OTP codes (configurable, default: 6) */
    @Value("${app.otp.length:6}")
    private int otpLength = 6;

    /** OTP expiry time in minutes (configurable, default: 5) */
    @Value("${app.otp.expiry-minutes:5}")
    private int otpExpiryMinutes = 5;
    
    /** OTP expiry time in seconds (calculated from minutes) */
    private long otpExpirySeconds = 300;
    
    /**
     * Sets the OTP expiry time in seconds (for testing purposes only).
     * 
     * @param otpExpirySeconds OTP validity period in seconds
     */
    public void setOtpExpirySeconds(long otpExpirySeconds) {
        this.otpExpirySeconds = otpExpirySeconds;
    }

    /**
     * Initializes the service by calculating the OTP expiry time in seconds.
     * This method is automatically called after dependency injection is done.
     */
    @PostConstruct
    public void init() {
        if (otpExpiryMinutes > 0) {
            this.otpExpirySeconds = otpExpiryMinutes * 60L;
        }
        log.debug("OTP Service initialized with OTP length: {}, Expiry: {} seconds", 
                 otpLength, otpExpirySeconds);
    }

    /**
     * Generates a new OTP for the specified user and stores it for validation.
     * The generated OTP will be valid for the configured expiry period.
     *
     * @param userId the unique identifier for the user
     * @return the generated OTP as a string
     * @throws IllegalArgumentException if userId is null or empty
     */
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

    /**
     * Validates the provided OTP for the given user.
     * If the OTP is valid and not expired, it will be removed from the store.
     * Expired OTPs are automatically cleaned up.
     *
     * @param userId the user ID to validate the OTP for
     * @param otp the OTP to validate
     * @return true if the OTP is valid and not expired, false otherwise
     */
    public boolean validateOtp(String userId, String otp) {
        // Early return for null inputs
        if (userId == null || otp == null) {
            return false;
        }
        
        // Retrieve the OTP data for the user (thread-safe operation)
        OtpData otpData = otpStore.get(userId);
        if (otpData == null) {
            log.debug("No OTP found for user: {}", userId);
            return false;
        }
        
        // Check if the provided OTP matches the stored one and is not expired
        boolean isOtpValid = otp.equals(otpData.getOtp());
        boolean isNotExpired = otpData.getExpiryTime() > Instant.now().getEpochSecond();
        
        // If OTP is valid and not expired, remove it from store (one-time use)
        if (isOtpValid && isNotExpired) {
            // Remove the OTP after successful validation (prevent replay attacks)
            otpStore.remove(userId);
            log.debug("Valid OTP for user: {}", userId);
            return true;
        }
        
        // Handle invalid or expired OTP cases
        if (!isNotExpired) {
            log.debug("OTP expired for user: {}", userId);
            // Clean up expired OTP to prevent memory leaks
            otpStore.remove(userId);
        } else if (!isOtpValid) {
            log.debug("Invalid OTP for user: {}", userId);
            // Note: We don't remove on invalid OTP to prevent user enumeration attacks
            // by revealing whether a user has a pending OTP or not
        }
        
        return false;
    }

    /**
     * Asynchronously removes all expired OTPs from the store.
     * This method is thread-safe and can be called concurrently.
     */
    /**
     * Asynchronously scans and removes all expired OTPs from the store.
     * This method runs on a scheduled basis to prevent memory leaks from expired OTPs.
     * 
     * <p>Execution details:
     * <ul>
     *   <li>Runs every 5 minutes (300,000 milliseconds)</li>
     *   <li>Executes asynchronously in a separate thread</li>
     *   <li>Thread-safe operation using ConcurrentHashMap</li>
     * </ul>
     */
    @Async
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void clearExpiredOtps() {
        long currentTime = Instant.now().getEpochSecond();
        // Remove all entries where the expiry time is in the past
        // Uses removeIf which is atomic and thread-safe with ConcurrentHashMap
        otpStore.entrySet().removeIf(entry -> 
            entry.getValue().getExpiryTime() <= currentTime
        );
        log.trace("Cleaned up expired OTPs. Current OTP store size: {}", otpStore.size());
    }

    /**
     * Immutable inner class to store OTP data along with its expiry time.
     * This ensures thread-safety as the data cannot be modified after creation.
     */
    private static class OtpData {
        // The one-time password value
        private final String otp;
        // Expiry time in seconds since epoch
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

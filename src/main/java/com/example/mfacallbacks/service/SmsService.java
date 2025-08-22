package com.example.mfacallbacks.service;

import com.example.mfacallbacks.exception.SmsException;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling SMS-related operations, particularly for sending OTPs.
 * This service integrates with the Twilio API to send SMS messages asynchronously.
 * 
 * <p>Configuration is done through application properties:
 * <ul>
 *   <li>twilio.phone-number: The Twilio phone number to send messages from</li>
 *   <li>app.otp.message: Template for OTP messages (use {otp} as placeholder)</li>
 *   <li>app.otp.expiry-minutes: OTP expiry time in minutes (default: 5)</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SmsService {

    /** Twilio phone number configured in application properties */
    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    /** Message template for OTP, should contain {otp} placeholder */
    @Value("${app.otp.message}")
    private String otpMessageTemplate;

    /** OTP expiry time in minutes, with default value of 5 minutes */
    @Value("${app.otp.expiry-minutes:5}")
    private int otpExpiryMinutes;

    /**
     * Sends an OTP to the specified phone number asynchronously.
     * This method is non-blocking and will return immediately.
     *
     * @param phoneNumber The recipient's phone number in E.164 format (e.g., "+1234567890")
     * @param otp The one-time password to send
     * @throws SmsException if there's an error sending the SMS
     */
    @Async
    public void sendOtp(String phoneNumber, String otp) {
        try {
            String message = String.format(otpMessageTemplate, otp, otpExpiryMinutes);
            
            Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioPhoneNumber),
                message
            ).create();
            
            log.info("OTP sent to {}: {}", phoneNumber, message);
        } catch (Exception e) {
            log.error("Failed to send OTP to " + phoneNumber, e);
            throw new SmsException("Failed to send OTP", e);
        }
    }
}

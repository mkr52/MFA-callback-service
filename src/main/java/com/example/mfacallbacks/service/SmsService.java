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

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsService {

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @Value("${app.otp.message}")
    private String otpMessageTemplate;

    @Value("${app.otp.expiry-minutes:5}")
    private int otpExpiryMinutes;

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

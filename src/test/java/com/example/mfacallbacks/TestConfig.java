package com.example.mfacallbacks;

import com.example.mfacallbacks.service.OtpService;
import com.example.mfacallbacks.service.SmsService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public OtpService otpService() {
        return new OtpService();
    }

    @Bean
    @Primary
    public SmsService smsService() {
        SmsService smsService = mock(SmsService.class);
        // Configure mock behavior here if needed
        Mockito.doNothing().when(smsService).sendOtp(Mockito.anyString(), Mockito.anyString());
        return smsService;
    }
}

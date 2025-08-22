package com.example.mfacallbacks;

import com.example.mfacallbacks.service.OtpService;
import com.example.mfacallbacks.service.SmsService;
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
        return mock(SmsService.class);
    }
}

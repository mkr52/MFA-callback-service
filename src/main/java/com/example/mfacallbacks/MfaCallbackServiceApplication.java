package com.example.mfacallbacks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MfaCallbackServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MfaCallbackServiceApplication.class, args);
    }
}

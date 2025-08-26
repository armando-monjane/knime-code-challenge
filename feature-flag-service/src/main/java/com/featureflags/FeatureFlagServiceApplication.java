package com.featureflags;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FeatureFlagServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeatureFlagServiceApplication.class, args);
    }
}

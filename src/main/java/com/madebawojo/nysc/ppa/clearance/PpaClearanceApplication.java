package com.madebawojo.nysc.ppa.clearance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableRetry
@SpringBootApplication
public class PpaClearanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PpaClearanceApplication.class, args);
    }
}

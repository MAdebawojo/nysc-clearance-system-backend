package com.madebawojo.nysc.ppa.clearance.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "app.security.password")
@Getter
@Setter
public class TokenProperties {
    private Duration setupTokenExpiry = Duration.ofHours(24); // fallback default
    private Duration resetTokenExpiry = Duration.ofHours(1);  // fallback default
    private Duration accessTokenExpiry = Duration.ofMinutes(30); // 30 minutes by default
}


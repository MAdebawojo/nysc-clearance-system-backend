package com.madebawojo.nysc.ppa.clearance.config.properties;

import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "global.admin")
public class GlobalAdminProperties {
    private String email;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
}


package com.madebawojo.nysc.ppa.clearance.seeder;

import com.madebawojo.nysc.ppa.clearance.config.properties.GlobalAdminProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalAdminSeeder implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder encoder;
    private final GlobalAdminProperties gaProperties;

    @Override
    public void run(String... args) {
        // Check if global admin exists
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                Integer.class,
                gaProperties.getEmail()
        );

        if (count != null && count == 0) {
            String hashedPassword = encoder.encode(gaProperties.getPassword());

            jdbcTemplate.update(
                    "INSERT INTO users (email, password, first_name, last_name, role, is_blocked, is_verified) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    gaProperties.getEmail(), hashedPassword, gaProperties.getFirstName(), gaProperties.getLastName(), gaProperties.getRole(), false, true
            );
            log.info("✅ Global admin seeded with email: {}", gaProperties.getEmail());
        } else {
            log.info("ℹ️ Global admin already exists, skipping seeding (email: {})", gaProperties.getEmail());
        }
    }
}

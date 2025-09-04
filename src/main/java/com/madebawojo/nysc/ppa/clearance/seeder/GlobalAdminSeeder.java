package com.madebawojo.nysc.ppa.clearance.seeder;

import com.madebawojo.nysc.ppa.clearance.config.properties.GlobalAdminProperties;
import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class GlobalAdminSeeder implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final GlobalAdminProperties gaProperties;

    @Override
    public void run(String... args) {
        // Check if global admin exists
        boolean exists = userRepository.existsByEmail(gaProperties.getEmail());

        if (!exists) {
            String hashedPassword = encoder.encode(gaProperties.getPassword());

            User user = User.builder()
                    .email(gaProperties.getEmail())
                    .password(hashedPassword)
                    .firstName(gaProperties.getFirstName())
                    .lastName(gaProperties.getLastName())
                    .role(Role.GLOBAL_ADMIN)
                    .isVerified(true)
                    .build();

            userRepository.save(user);

            log.info("✅ Global admin seeded with email: {}", gaProperties.getEmail());
        } else {
            log.info("ℹ️ Global admin already exists, skipping seeding (email: {})", gaProperties.getEmail());
        }
    }
}

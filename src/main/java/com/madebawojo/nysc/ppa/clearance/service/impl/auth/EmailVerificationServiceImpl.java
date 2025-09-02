package com.madebawojo.nysc.ppa.clearance.service.impl.auth;

import com.madebawojo.nysc.ppa.clearance.entity.VerificationToken;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.repository.UserRepository;
import com.madebawojo.nysc.ppa.clearance.repository.VerificationTokenRepository;
import com.madebawojo.nysc.ppa.clearance.service.impl.EmailServiceImpl;
import com.madebawojo.nysc.ppa.clearance.util.OpaqueTokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailVerificationServiceImpl {
    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;
    private final OpaqueTokenUtil tokenUtil;
//    private final VerificationToken verificationToken;
    private final VerificationTokenRepository tokenRepository;

    public void generateVerificationToken(User user){
        String rawToken = tokenUtil.generateRawToken();
        VerificationToken token = VerificationToken.builder()
                .tokenHash(tokenUtil.hash(rawToken))
                .expiresAt(LocalDateTime.now().plusDays(30))
                .user(user)
                .build();
        tokenRepository.save(token);
        log.info("raw token for testing is: {}", rawToken);
        sendVerificationEmail(user, "http://localhost:8080/api/v1/auth/verify-email?token=" + rawToken);
    }
    public void verifyUserByToken(String token) {
        VerificationToken vt = tokenRepository.findByTokenHash(tokenUtil.hash(token))
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (vt.isUsed()) {
            throw new RuntimeException("Token already used");
        }

        if (vt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        User user = vt.getUser();
        user.setVerified(true);
        userRepository.save(user);

        vt.setUsed(true);
        vt.setUsedAt(LocalDateTime.now());
        tokenRepository.save(vt);
    }

    private void sendVerificationEmail(User user, String verificationLink) {
        String htmlTemplate = emailService.loadTemplate("verify-email.html");

        Map<String, String> placeholders = Map.of(
                "{{name}}", user.getFirstName(),
                "{{verification_link}}", verificationLink
        );

        String content = emailService.replacePlaceholders(htmlTemplate, placeholders);

        log.info("Sending Email Verification to {}", user.getEmail());

        emailService.sendEmailAsync(user.getEmail(), "Verification Email Request", content);
    }
}

package com.madebawojo.nysc.ppa.clearance.service.impl.auth;

import com.madebawojo.nysc.ppa.clearance.core.enums.PasswordTokenType;
import com.madebawojo.nysc.ppa.clearance.core.exception.ApiException;
import com.madebawojo.nysc.ppa.clearance.core.exception.ResourceNotFoundException;
import com.madebawojo.nysc.ppa.clearance.entity.auth.PasswordToken;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.repository.PasswordTokenRepository;
import com.madebawojo.nysc.ppa.clearance.repository.UserRepository;
import com.madebawojo.nysc.ppa.clearance.service.impl.EmailServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth.PasswordResetService;
import com.madebawojo.nysc.ppa.clearance.util.AppConstants;
import com.madebawojo.nysc.ppa.clearance.util.OpaqueTokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final AuthServiceImpl authService;
    private final EmailServiceImpl emailService;
    private final OpaqueTokenUtil tokenUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordTokenRepository tokenRepository;

    @Override
    public void requestPasswordReset(String email) {
        User user = authService.getUserByEmail(email);

        invalidateOldTokens(user);

        String rawToken = tokenUtil.generateRawToken();

        PasswordToken token = PasswordToken.builder()
                .tokenHash(tokenUtil.hash(rawToken))
                .expiresAt(LocalDateTime.now().plusHours(1))
                .user(user)
                .type(PasswordTokenType.RESET)
                .build();

        tokenRepository.save(token);

        String resetLink = AppConstants.FRONTEND_BASE_URL + "/auth/reset-password?token=" + rawToken;

        log.info("Password reset requested for user {}", user.getEmail());

        emailService.sendResetPasswordEmail(user, resetLink);
    }

    @Override
    public PasswordToken validateToken(String token, PasswordTokenType expectedType) {
        PasswordToken rt = tokenRepository.findByTokenHashAndType(tokenUtil.hash(token), expectedType)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid token or wrong token type"));

        if (rt.isUsed()) {
            throw new ApiException("Token already used", HttpStatus.BAD_REQUEST);
        }

        if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Token has expired", HttpStatus.UNAUTHORIZED);
        }

        return rt;
    }

    @Override
    public void resetPassword(String rawToken, String newPassword) {
        PasswordToken rt = validateToken(rawToken, PasswordTokenType.RESET);

        User user = rt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        rt.setUsed(true);
        rt.setUsedAt(LocalDateTime.now());
        tokenRepository.save(rt);

        log.info("Password successfully reset for user {}", user.getEmail());
    }

    @Override
    public void invalidateOldTokens(User user) {
        tokenRepository.deleteByUserAndType(user, PasswordTokenType.RESET);
    }

    @Override
    public void cleanUpExpiredTokens() {

    }

//    @Override
//    public void cleanUpExpiredTokens() {
//        int deleted = tokenRepository.deleteAllExpiredSince(LocalDateTime.now());
//        log.info("Cleaned up {} expired reset tokens", deleted);
//    }
}

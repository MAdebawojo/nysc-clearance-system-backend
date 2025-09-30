package com.madebawojo.nysc.ppa.clearance.service.impl.auth;

import com.madebawojo.nysc.ppa.clearance.core.enums.PasswordTokenType;
import com.madebawojo.nysc.ppa.clearance.core.exception.ApiException;
import com.madebawojo.nysc.ppa.clearance.core.exception.ResourceNotFoundException;
import com.madebawojo.nysc.ppa.clearance.dto.request.auth.SetupPasswordRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.AuthenticationResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.auth.PasswordToken;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.repository.PasswordTokenRepository;
import com.madebawojo.nysc.ppa.clearance.repository.UserRepository;
import com.madebawojo.nysc.ppa.clearance.service.impl.EmailServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth.PasswordSetupService;
import com.madebawojo.nysc.ppa.clearance.util.AppConstants;
import com.madebawojo.nysc.ppa.clearance.util.OpaqueTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordSetupServiceImpl implements PasswordSetupService {

    private final EmailServiceImpl emailService;
    private final OpaqueTokenUtil tokenUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordTokenRepository tokenRepository;

    private final RefreshTokenServiceImpl refreshService;


    @Override
    public void requestPasswordSetup(User user) {
        // Invalidate any old SETUP tokens
        tokenRepository.deleteByUserAndType(user, PasswordTokenType.SETUP);

        String rawToken = tokenUtil.generateRawToken();

        PasswordToken token = PasswordToken.builder()
                .tokenHash(tokenUtil.hash(rawToken))
                .expiresAt(LocalDateTime.now().plusDays(1)) // maybe longer for invites
                .user(user)
                .type(PasswordTokenType.SETUP)
                .build();

        tokenRepository.save(token);

        String setupLink = AppConstants.FRONTEND_BASE_URL + "/setup-password?token=" + rawToken;

        log.info("Password setup link created for new user {}", user.getEmail());

        log.info("View link: {}", setupLink);

        emailService.sendSetupPasswordEmail(user, setupLink);
    }

    @Override
    public PasswordToken validateToken(String token) {
        PasswordToken pt = tokenRepository.findByTokenHashAndType(tokenUtil.hash(token), PasswordTokenType.SETUP)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or wrong token type"));

        if (pt.isUsed()) {
            throw new ApiException("Token already used", HttpStatus.BAD_REQUEST);
        }

        if (pt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Token has expired", HttpStatus.UNAUTHORIZED);
        }

        return pt;
    }

    @Override
    public AuthenticationResponseDto setupPassword(SetupPasswordRequestDto dto, String ip, String ua) {
        PasswordToken pt = validateToken(dto.getSetupToken());

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ApiException("Passwords do not match", HttpStatus.BAD_REQUEST);
        }

        User user = pt.getUser();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        pt.setUsed(true);
        pt.setUsedAt(LocalDateTime.now());
        tokenRepository.save(pt);

        user.setVerified(true); // is_verified = true
        userRepository.save(user);

        // issue tokens (auto-login)
        TokenResponse tokenResponse = refreshService.issueAuthTokens(user, ip, ua);

        log.info("Password successfully set for new user {}", user.getEmail());

        return AuthenticationResponseDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .authTokens(tokenResponse)
                .build();
    }

    @Override
    public void resendSetupLink(String email) {
        log.debug("Attempting to resend setup link for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Resend setup link failed - user not found for email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        if (user.getPassword() != null) {
            throw new ApiException("Password already set. Please login or reset password.", HttpStatus.BAD_REQUEST);
        }

        // Invalidate previous tokens
        tokenRepository.deleteByUserAndType(user, PasswordTokenType.SETUP);

        String rawToken = tokenUtil.generateRawToken();

        PasswordToken token = PasswordToken.builder()
                .tokenHash(tokenUtil.hash(rawToken))
                .expiresAt(LocalDateTime.now().plusHours(24)) // setup links last longer
                .user(user)
                .type(PasswordTokenType.SETUP)
                .build();

        tokenRepository.save(token);

        log.debug("Created new password setup token for user {} with expiry at {}", user.getEmail(), token.getExpiresAt());

        String setupLink = AppConstants.FRONTEND_BASE_URL + "/setup-password?token=" + rawToken;

        emailService.sendSetupPasswordEmail(user, setupLink);

        log.info("Resent password setup link for user {}", user.getEmail());
    }

}


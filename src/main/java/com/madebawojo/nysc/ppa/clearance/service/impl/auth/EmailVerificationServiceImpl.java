package com.madebawojo.nysc.ppa.clearance.service.impl.auth;

import com.madebawojo.nysc.ppa.clearance.core.exception.ApiException;
import com.madebawojo.nysc.ppa.clearance.core.exception.ResourceNotFoundException;
import com.madebawojo.nysc.ppa.clearance.entity.auth.VerificationToken;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.repository.UserRepository;
import com.madebawojo.nysc.ppa.clearance.repository.VerificationTokenRepository;
import com.madebawojo.nysc.ppa.clearance.service.impl.EmailServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth.EmailVerificationService;
import com.madebawojo.nysc.ppa.clearance.util.OpaqueTokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import com.madebawojo.nysc.ppa.clearance.util.AppConstants;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;
    private final OpaqueTokenUtil tokenUtil;
    private final AuthServiceImpl authService;
    private final VerificationTokenRepository tokenRepository;

    @Override
    public void generateAndSendVerificationToken(User user){
        String rawToken = tokenUtil.generateRawToken();

        invalidateOldTokens(user);

        VerificationToken token = VerificationToken.builder()
                .tokenHash(tokenUtil.hash(rawToken))
                .expiresAt(LocalDateTime.now().plusDays(3))
                .user(user)
                .build();

        tokenRepository.save(token);
//        log.info("raw token for testing is: {}", rawToken);
        log.info("Sending email verification link to {}", user.getEmail());

        String verifyLink = AppConstants.FRONTEND_BASE_URL + "/auth/verify-email?token=" + rawToken;

        emailService.sendVerificationEmail(user, verifyLink);
    }

    @Override
    public void verifyUserByToken(String token) {
        VerificationToken vt = tokenRepository.findByTokenHash(tokenUtil.hash(token))
                .orElseThrow(() -> new ResourceNotFoundException("Invalid token"));

        if (vt.isUsed()) {
            throw new ApiException("Token already used", HttpStatus.BAD_REQUEST);
        }

        if (vt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Token has expired", HttpStatus.BAD_REQUEST);
        }

        User user = vt.getUser();
        user.setVerified(true);
        userRepository.save(user);

        vt.setUsed(true);
        vt.setUsedAt(LocalDateTime.now());
        tokenRepository.save(vt);
    }

    @Override
    public boolean resendVerificationToken(String email) {
      log.info("Resending email verification for {}", email);

      User user =  authService.getUserByEmail(email);
      if(user.isVerified()){
          log.info("User {} already verified", user.getEmail());
          return false;
      }

      generateAndSendVerificationToken(user);
      return true;
    }

    @Override
    public void invalidateOldTokens(User user) {
        tokenRepository.deleteByUser(user);
    }
}

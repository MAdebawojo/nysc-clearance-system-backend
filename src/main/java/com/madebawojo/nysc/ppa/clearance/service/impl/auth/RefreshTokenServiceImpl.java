package com.madebawojo.nysc.ppa.clearance.service.impl.auth;

import com.madebawojo.nysc.ppa.clearance.core.exception.UnauthorizedException;
import com.madebawojo.nysc.ppa.clearance.dto.response.AuthenticationResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.auth.RefreshToken;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.repository.RefreshTokenRepository;
import com.madebawojo.nysc.ppa.clearance.util.JwtUtil;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth.RefreshTokenService;
import com.madebawojo.nysc.ppa.clearance.util.OpaqueTokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository repo;
    private final OpaqueTokenUtil tok;
    private final JwtUtil jwtUtil;

    // Issue at login
    @Override
    public TokenResponse issueAuthTokens(User user, String ip, String ua) {
        String familyId = tok.newFamilyId();
        TokenIssueResult result = createAndReturn(user, familyId, ip, ua);
        return result.getTokenResponse();
    }

    // Refresh (rotation + reuse detection)
    @Override
    public AuthenticationResponseDto refresh(String presentedRawToken, String ip, String ua) {
        String hash = tok.hash(presentedRawToken);
        RefreshToken current = repo.findByTokenHash(hash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        // Basic checks
        if (current.isRevoked()) throw new UnauthorizedException("Refresh token revoked");
        if (Instant.now().isAfter(current.getExpiresAt())) {
            revoke(current, "expired");
            throw new UnauthorizedException("Refresh token expired");
        }

        // ROTATION: revoke current and create a new one in same family
        current.setRevoked(true);
        current.setRevokedAt(Instant.now());
        current.setRevokeReason("rotated");

        TokenIssueResult tokenIssueResult = createAndReturn(current.getUser(), current.getFamilyId(), ip, ua);
        // link for audit
        repo.flush();
        current.setReplacedById(tokenIssueResult.getRefreshEntity().getReplacedById());

        User user = current.getUser();
        return AuthenticationResponseDto.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .authTokens(tokenIssueResult.getTokenResponse())
                .build();
    }

    // Logout this device (revoke this token only)
//    @Override
//    public void logout(String rawRefreshToken) {
//        repo.findByTokenHash(tok.hash(rawRefreshToken))
//                .ifPresent(rt -> revoke(rt, "logout"));
//
////        log.info("logging out. Presented Token {}", presentedRawToken);
////        Optional<RefreshToken> rt = repo.findByTokenHash(tok.hash(presentedRawToken));
////        if(rt.isPresent()){
////            log.info("Refresh Token Found! {}", rt);
////        }
////        else{
////            log.error("Refresh Token not found. See Hash conversion {}", tok.hash(presentedRawToken));
////        }
//    }

    @Override
    public void logout(String rawRefreshToken) {
        log.info("Log out invoked");
        String tokenHash = tok.hash(rawRefreshToken);

        Optional<RefreshToken> optionalToken = repo.findByTokenHash(tokenHash);

        if (optionalToken.isPresent()) {
            RefreshToken rt = optionalToken.get();
            revoke(rt, "logout");
            log.info("Refresh token [{}] successfully revoked due to logout.", truncateHash(tokenHash));
        } else {
            log.warn("Logout attempt with unknown or already-revoked token [{}].", truncateHash(tokenHash));
        }
    }


    // Logout all devices (revoke active tokens for user)
    @Override
    public void logoutAll(User user) {
        List<RefreshToken> activeTokens = repo.findAllByUserIdAndRevokedFalse(user.getId());

        if (activeTokens.isEmpty()) {
            log.info("LogoutAll requested for user [{}], but no active refresh tokens found.", user.getId());
            return;
        }

        activeTokens.forEach(rt -> revoke(rt, "logout_all"));

        log.info("LogoutAll: Revoked {} refresh tokens for user [{}].", activeTokens.size(), user.getId());
    }

//    @Override
//    public void logoutAll(User user) {
//        repo.findAllByUserIdAndRevokedFalse(user.getId())
//                .forEach(rt -> revoke(rt, "logout_all"));
//    }

    // --- helpers ---
    private TokenIssueResult createAndReturn(User user, String familyId, String ip, String ua) {
        // 1) short-lived access token (e.g., 15m)
        Map<String, Object> extraClaims = generateClaims(user);
        String accessToken = jwtUtil.generateToken(extraClaims, user);
        long accessTokenExpiry = jwtUtil.getExpirationTimeInSeconds(accessToken);

        // 2) opaque refresh token (e.g., 30d)
        String rawRefresh = tok.generateRawToken();
        RefreshToken rt = new RefreshToken();
        rt.setTokenHash(tok.hash(rawRefresh));
        rt.setUser(user);
        rt.setFamilyId(familyId);
        rt.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        rt.setUserAgent(ua);
        rt.setIp(ip);
        repo.save(rt);

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefresh)
                .tokenType("Bearer")
                .accessTokenExpiry(accessTokenExpiry)
                .build();

        return TokenIssueResult.builder()
                .tokenResponse(tokenResponse)
                .refreshEntity(rt)
                .build();
    }

    private void revoke(RefreshToken rt, String reason) {
        rt.setRevoked(true);
        rt.setRevokedAt(Instant.now());
        rt.setRevokeReason(reason);
        log.info("Revoking token: {}", rt.getId());
        repo.save(rt);
        log.info("Token revoked");

    }

    private Map<String, Object> generateClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("role", user.getRole().name());
        return claims;
    }

    private String truncateHash(String hash) {
        return hash.length() > 8 ? hash.substring(0, 8) + "..." : hash;
    }
}
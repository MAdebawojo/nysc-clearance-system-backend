package com.madebawojo.nysc.ppa.clearance.service.impl.auth;

import com.madebawojo.nysc.ppa.clearance.core.exception.ApiException;
import com.madebawojo.nysc.ppa.clearance.core.exception.ResourceNotFoundException;
import com.madebawojo.nysc.ppa.clearance.core.exception.UnauthorizedException;
import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
import com.madebawojo.nysc.ppa.clearance.dto.request.auth.AuthenticationRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.AuthenticationResponseDto;
import com.madebawojo.nysc.ppa.clearance.repository.UserRepository;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenServiceImpl refreshService;
//    private final EmailVerificationServiceImpl emailVerificationService;

    @Override
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request, String ip, String ua) {
        var user = getUserByEmail(request.getEmail());

        if (!user.isEnabled()){
            log.warn("Login attempt for disabled account: {}", user.getEmail());
//            emailVerificationService.resendVerificationToken(user.getEmail());
            throw new ApiException("Your account is not yet enabled. Please check your email for a verification link.", HttpStatus.FORBIDDEN);
        }

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex){
            throw new UnauthorizedException("Invalid email or password");
        }

        TokenResponse tokenResponse = refreshService.issueAuthTokens(user, ip, ua);

        log.info("User {} is authenticated", user.getEmail());
        return AuthenticationResponseDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .authTokens(tokenResponse)
                .build();
    }

    public User getUserByEmail(String email) {
        log.info("Retrieving user with email {} from AuthService", email);
        return userRepository.findByEmail(email)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User getUserById(Long userId) {
        log.info("Retrieving user with id {} from AuthService", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userId));
    }


    //    @Override
//    public AuthenticationResponseDto authenticate(User user, String ip, String ua) {
//        try{
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            user.getEmail(),
//                            user.getPassword()
//                    )
//            );
//        } catch (BadCredentialsException ex){
//            throw new UnauthorizedException("Invalid email or password");
//        }
//
////        var user = getUserByEmail(requestUser.getEmail());
//
////        Map<String, Object> extraClaims = generateClaims(user);
////        var jwtToken = jwtUtil.generateToken(extraClaims, user);
//        return refreshService.issueFor(user, ip, ua);
//
////        return AuthenticationResponseDto.builder()
////                .token(jwtToken)
////                .email(user.getEmail())
////                .role(user.getRole())
////                .build();
//    }
    //    public AuthenticationResponseDto register(RegisterRequest request) {
//        var user = User.builder()
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(Role.CORPER)
//                .build();
//
//        userRepository.save(user);
//
//        Map<String, Object> extraClaims = generateClaims(user);
//        var jwtToken = jwtService.generateToken(extraClaims, user);
//
//        return AuthenticationResponseDto.builder()
//                .token(jwtToken)
//                .build();
//    }

    /** Helper function to factory claims */
    private Map<String, Object> generateClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("role", user.getRole().name());

        if (user.getRole() == Role.SUPER_ADMIN) {
            if (user.getSuperAdmin() == null) {
                throw new ApiException("This user is not a super admin", HttpStatus.FORBIDDEN);
            }
            claims.put("ppaId", user.getSuperAdmin().getPpa().getId());
        }
        if (user.getRole() == Role.ADMIN) {
            if (user.getAdmin() == null) {
                throw new ApiException("This user is not an admin", HttpStatus.FORBIDDEN);
            }
            claims.put("unitId", user.getAdmin().getPpa().getId());
        }

        return claims;
    }
}

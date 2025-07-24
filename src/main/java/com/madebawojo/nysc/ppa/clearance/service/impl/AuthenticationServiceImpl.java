package com.madebawojo.nysc.ppa.clearance.service.impl;

import com.madebawojo.nysc.ppa.clearance.core.exception.ApiException;
import com.madebawojo.nysc.ppa.clearance.core.exception.ResourceNotFoundException;
import com.madebawojo.nysc.ppa.clearance.core.exception.UnauthorizedException;
import com.madebawojo.nysc.ppa.clearance.security.JwtService;
import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
import com.madebawojo.nysc.ppa.clearance.dto.request.AuthenticationRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.AuthenticationResponseDto;
import com.madebawojo.nysc.ppa.clearance.repository.UserRepository;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.AuthenticationService;
import lombok.RequiredArgsConstructor;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
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

        var user = userRepository.findByEmail(request.getEmail())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        Map<String, Object> extraClaims = generateClaims(user);
        var jwtToken = jwtService.generateToken(extraClaims, user);

        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }


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

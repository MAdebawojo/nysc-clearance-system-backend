package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.request.*;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.response.AuthenticationResponseDto;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.AuthServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.EmailVerificationServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.PasswordResetServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.RefreshTokenServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controllers", description = "Endpoints for user authentication and account management")
public class AuthController {

    private final AuthServiceImpl authService;
    private final RefreshTokenServiceImpl refreshService;
    private final PasswordResetServiceImpl passwordResetService;
    private final EmailVerificationServiceImpl emailVerificationService;

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticates a user and returns JWT tokens upon successful login."
    )
    public ResponseEntity<ApiResponseStructure<AuthenticationResponseDto>> login(
            @Valid @RequestBody AuthenticationRequestDto req,
            HttpServletRequest servletReq
    ) {
        AuthenticationResponseDto authResponse = authService.authenticate(
                req,
                servletReq.getRemoteAddr(),
                servletReq.getHeader("User-Agent")
        );

        return ResponseEntity.ok(ApiResponseStructure.success("Login successful", authResponse, 200));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh JWT tokens",
            description = "Uses a refresh token to generate a new access token and refresh token pair."
    )
    public ResponseEntity<ApiResponseStructure<AuthenticationResponseDto>> refresh(
            @Valid
            @RequestBody RefreshTokenRequest request,
            HttpServletRequest servletReq
    ) {
        String token = request.getRefreshToken();
        AuthenticationResponseDto out = refreshService.refresh(
                token, servletReq.getRemoteAddr(), servletReq.getHeader("User-Agent")
        );
        return ResponseEntity.ok(ApiResponseStructure.success("Token refreshed", out, 200));
    }

    @GetMapping("/verify-email")
    @Operation(
            summary = "Verify user email",
            description = "Verifies a user's email address using a provided verification token."
    )
    public ResponseEntity<ApiResponseStructure<String>> verifyEmail(@RequestParam String token) {
        emailVerificationService.verifyUserByToken(token);
        return ResponseEntity.ok(ApiResponseStructure.success("Email verified successfully", null, 200));
    }

    @PostMapping("/resend-verification")
    @Operation(
            summary = "Resend verification email",
            description = "Resends the email verification token to a user whose email has not yet been verified."
    )
    public ResponseEntity<ApiResponseStructure<String>> resendVerification(@RequestBody ResendVerificationDto dto) {
        boolean sent = emailVerificationService.resendVerificationToken(dto.getEmail());
        if (sent) {
            return ResponseEntity.ok(ApiResponseStructure.success("Verification email resent", null, 200));
        } else {
            return ResponseEntity.ok(ApiResponseStructure.success("User already verified", null, 200));
        }
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Initiate password reset",
            description = "Sends a password reset link to the email of a user who has forgotten their password."
    )
    public ResponseEntity<ApiResponseStructure<String>> forgotPassword(@RequestBody ForgotPasswordRequestDto requestDto) {
        passwordResetService.requestPasswordReset(requestDto.getEmail());
        return ResponseEntity.ok(ApiResponseStructure.success("If an account with that email exists, a reset link has been sent", null, 200));
    }

    @GetMapping("/reset-password/validate")
    @Operation(
            summary = "Validate password reset token",
            description = "Validates the token sent to the user's email for password reset."
    )
    public ResponseEntity<ApiResponseStructure<String>> validateToken(@RequestParam String token) {
        passwordResetService.validateToken(token);
        return ResponseEntity.ok(ApiResponseStructure.success("Token is valid", null, 200));
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset user password",
            description = "Resets the user's password after validating the reset token and accepting the new password."
    )
    public ResponseEntity<ApiResponseStructure<String>> resetPassword(@RequestBody ResetPasswordRequestDto requestDto) {
        passwordResetService.resetPassword(requestDto.getToken(), requestDto.getNewPassword());
        return ResponseEntity.ok(ApiResponseStructure.success("Password successfully reset", null, 200));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Log out a single device",
            description = "Invalidates the refresh token for a specific device, effectively logging the user out of that session."
    )
    public ResponseEntity<ApiResponseStructure<String>> logout(@RequestBody RefreshTokenRequest request) {
        refreshService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponseStructure.success("Logged out (this device)", null, 200));
    }

    @PostMapping("/logout-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Log out all devices",
            description = "Logs the user out of all active sessions by invalidating all associated refresh tokens."
    )
    public ResponseEntity<ApiResponseStructure<String>> logoutAll(@AuthenticationPrincipal(expression = "id") Long userId) {
         refreshService.logoutAll(authService.getUserById(userId));
        return ResponseEntity.ok(ApiResponseStructure.success("Logged out of all devices", null, 200));
    }
//}


//    @PostMapping("/authenticate")
//    public ResponseEntity<ApiResponseStructure<AuthenticationResponseDto>> authenticate(
//            @RequestBody AuthenticationRequestDto request
//    ){
//        AuthenticationResponseDto response = authenticationService.authenticate(request);
//        return ResponseEntity.ok(
//                ApiResponseStructure.success("Login successful", response, HttpStatus.OK.value())
//        );
//    }

    //    @PostMapping("/register")
//    public ResponseEntity<AuthenticationResponseDto> register(
//            @RequestBody RegisterRequest request
//    ){
//        return ResponseEntity.ok(authenticationService.register(request));
//    }
}

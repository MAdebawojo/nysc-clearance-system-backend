package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.core.enums.PasswordTokenType;
import com.madebawojo.nysc.ppa.clearance.dto.request.auth.*;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.response.AuthenticationResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.AuthServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.EmailVerificationServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.PasswordResetServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.RefreshTokenServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth.PasswordSetupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final PasswordSetupService passwordSetupService;


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

    @Operation(
            summary = "Validate setup token",
            description = "Checks if a setup token is valid and not expired before allowing the user to set a password."
    )
    @GetMapping("/validate-setup-token")
    public ResponseEntity<ApiResponseStructure<Void>> validateSetupToken(@RequestParam String token) {
        passwordSetupService.validateToken(token);
        return ResponseEntity.ok(ApiResponseStructure.success("Token is valid", null, HttpStatus.OK.value()));
    }

    @Operation(
            summary = "Set up password for a new user",
            description = "Allows a new user to set their password using a valid setup token. Automatically logs them in upon success."
    )
    @PostMapping("/setup-password")
    public ResponseEntity<ApiResponseStructure<AuthenticationResponseDto>> setupPassword(
            @Valid @RequestBody SetupPasswordRequestDto dto,
            HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");

        AuthenticationResponseDto response = passwordSetupService.setupPassword(dto, ip, ua);

        return ResponseEntity.ok(ApiResponseStructure.success("Password setup successful", response, HttpStatus.OK.value()));
    }

    @PostMapping("/resend-setup")
    @Operation(
            summary = "Resend password setup email",
            description = "Resends a password setup email to a user who hasn’t set their password yet."
    )
    public ResponseEntity<ApiResponseStructure<Void>> resendSetupEmail(@Valid @RequestBody ResendSetupDto dto) {
        passwordSetupService.resendSetupLink(dto.getEmail());
        return ResponseEntity.ok(ApiResponseStructure.success("Password setup email resent", null, HttpStatus.OK.value()));
    }


    @PostMapping("/forgot-password")
    @Operation(
            summary = "Initiate password reset",
            description = "Sends a password reset link to the email of a user who has forgotten their password."
    )
    public ResponseEntity<ApiResponseStructure<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto requestDto) {
        passwordResetService.requestPasswordReset(requestDto.getEmail());
        return ResponseEntity.ok(ApiResponseStructure.success("If an account with that email exists, a reset link has been sent", null, 200));
    }

    @GetMapping("/reset-password/validate")
    @Operation(
            summary = "Validate password reset token",
            description = "Validates the token sent to the user's email for password reset."
    )
    public ResponseEntity<ApiResponseStructure<String>> validateResetToken(@RequestParam String token) {
        passwordResetService.validateToken(token, PasswordTokenType.RESET);
        return ResponseEntity.ok(ApiResponseStructure.success("Token is valid", null, 200));
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset user password",
            description = "Resets the user's password after validating the reset token and accepting the new password."
    )
    public ResponseEntity<ApiResponseStructure<String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDto requestDto) {
        passwordResetService.resetPassword(requestDto.getToken(), requestDto.getNewPassword());
        return ResponseEntity.ok(ApiResponseStructure.success("Password successfully reset", null, 200));
    }

    @PostMapping("/logout")
//    @PreAuthorize("isAuthenticated()")
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
    public ResponseEntity<ApiResponseStructure<String>> logoutAll(@AuthenticationPrincipal(expression = "id") User user) {
         refreshService.logoutAll(authService.getUserById(user.getId()));
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

    //    @GetMapping("/verify-email")
//    @Operation(
//            summary = "Verify user email",
//            description = "Verifies a user's email address using a provided verification token."
//    )
//    public ResponseEntity<ApiResponseStructure<String>> verifyEmail(@RequestParam String token) {
//        emailVerificationService.verifyUserByToken(token);
//        return ResponseEntity.ok(ApiResponseStructure.success("Email verified successfully", null, 200));
//    }

//    @PostMapping("/set-password")
//    @Operation(
//            summary = "Set password after account verification",
//            description = "Allows users without a password to set one after verifying their email."
//    )
//    public ResponseEntity<ApiResponseStructure<String>> setPassword(
//            @Valid @RequestBody SetPasswordRequestDto request
//    ) {
//        authService.setPassword(request.getToken(), request.getNewPassword());
//        return ResponseEntity.ok(ApiResponseStructure.success("Password set successfully", null, 200));
//    }

//    @PostMapping("/resend-verification")
//    @Operation(
//            summary = "Resend verification email",
//            description = "Resends the email verification token to a user whose email has not yet been verified."
//    )
//    public ResponseEntity<ApiResponseStructure<String>> resendVerification(@Valid @RequestBody ResendVerificationDto dto) {
//        boolean sent = emailVerificationService.resendVerificationToken(dto.getEmail());
//        if (sent) {
//            return ResponseEntity.ok(ApiResponseStructure.success("Verification email resent", null, 200));
//        } else {
//            return ResponseEntity.ok(ApiResponseStructure.success("User already verified", null, 200));
//        }
//    }
}

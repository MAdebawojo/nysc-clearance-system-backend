package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.request.AuthenticationRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.RefreshTokenRequest;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.response.AuthenticationResponseDto;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.AuthServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.RefreshTokenServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;
    private final RefreshTokenServiceImpl refreshService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseStructure<AuthenticationResponseDto>> login(
            @RequestBody AuthenticationRequestDto req,
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
    public ResponseEntity<ApiResponseStructure<AuthenticationResponseDto>> refresh(
            @RequestBody RefreshTokenRequest request,
            HttpServletRequest servletReq
    ) {
        String token = request.getRefreshToken();
        AuthenticationResponseDto out = refreshService.refresh(
                token, servletReq.getRemoteAddr(), servletReq.getHeader("User-Agent")
        );
        return ResponseEntity.ok(ApiResponseStructure.success("Token refreshed", out, 200));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseStructure<String>> logout(@RequestBody RefreshTokenRequest request) {
        refreshService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponseStructure.success("Logged out (this device)", null, 200));
    }

    @PostMapping("/logout-all")
    @PreAuthorize("isAuthenticated()")
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

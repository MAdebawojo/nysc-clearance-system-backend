package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.request.auth.UpdateCredentialsRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.UserServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserServiceImpl userService;

    @PutMapping("/credentials")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseStructure<String>> updateCredentials(@RequestBody UpdateCredentialsRequestDto request,
                                                                          @AuthenticationPrincipal User principal) {
        String response = userService.updateCredentials(principal.getEmail(), request);
        return ResponseEntity.ok(ApiResponseStructure.success(response, null, HttpStatus.NO_CONTENT.value()));
    }
}

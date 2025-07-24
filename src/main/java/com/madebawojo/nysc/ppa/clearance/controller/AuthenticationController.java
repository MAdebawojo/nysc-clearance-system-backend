package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.request.AuthenticationRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.response.AuthenticationResponseDto;
import com.madebawojo.nysc.ppa.clearance.service.impl.AuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationService;

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponseStructure<AuthenticationResponseDto>> authenticate(
            @RequestBody AuthenticationRequestDto request
    ){
        AuthenticationResponseDto response = authenticationService.authenticate(request);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Login successful", response, HttpStatus.OK.value())
        );
    }

    //    @PostMapping("/register")
//    public ResponseEntity<AuthenticationResponseDto> register(
//            @RequestBody RegisterRequest request
//    ){
//        return ResponseEntity.ok(authenticationService.register(request));
//    }
}

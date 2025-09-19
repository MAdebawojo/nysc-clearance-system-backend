package com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth;

import com.madebawojo.nysc.ppa.clearance.dto.request.auth.AuthenticationRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.AuthenticationResponseDto;

public interface AuthenticationService {
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request,  String ip, String ua);
//    public AuthenticationResponseDto register(RegisterRequest request);
}

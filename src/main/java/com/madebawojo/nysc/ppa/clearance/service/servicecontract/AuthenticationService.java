package com.madebawojo.nysc.ppa.clearance.service.servicecontract;

import com.madebawojo.nysc.ppa.clearance.dto.request.AuthenticationRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.AuthenticationResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;

import java.util.Map;

public interface AuthenticationService {
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request);
//    public AuthenticationResponseDto register(RegisterRequest request);
}

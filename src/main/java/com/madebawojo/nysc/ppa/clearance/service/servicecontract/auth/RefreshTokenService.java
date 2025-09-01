package com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth;

import com.madebawojo.nysc.ppa.clearance.dto.response.AuthenticationResponseDto;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.TokenResponse;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;

public interface RefreshTokenService {
    TokenResponse issueAuthTokens(User user, String ip, String ua);
    AuthenticationResponseDto refresh(String presentedRawToken, String ip, String ua);
    void logout(String presentedRawToken);
    void logoutAll(User user);
}


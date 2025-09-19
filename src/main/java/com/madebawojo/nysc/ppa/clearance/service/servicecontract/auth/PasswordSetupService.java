package com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth;

import com.madebawojo.nysc.ppa.clearance.dto.request.auth.SetupPasswordRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.AuthenticationResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.auth.PasswordToken;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface PasswordSetupService {
    void requestPasswordSetup(User user);
    PasswordToken validateToken(String token);
    AuthenticationResponseDto setupPassword(SetupPasswordRequestDto dto, String ip, String ua);

    void resendSetupLink(String email);
}


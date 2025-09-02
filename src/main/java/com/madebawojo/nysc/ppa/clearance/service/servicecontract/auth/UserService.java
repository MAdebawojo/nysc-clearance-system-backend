package com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth;

import com.madebawojo.nysc.ppa.clearance.dto.request.UpdateCredentialsRequestDto;

public interface UserService {
    public String updateCredentials(String email, UpdateCredentialsRequestDto request);
}

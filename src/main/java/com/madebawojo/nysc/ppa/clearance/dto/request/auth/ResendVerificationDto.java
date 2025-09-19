package com.madebawojo.nysc.ppa.clearance.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResendVerificationDto {
    @NotNull
    @Email
    private String email;
}

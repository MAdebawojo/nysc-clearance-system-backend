package com.madebawojo.nysc.ppa.clearance.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequestDto {
    @JsonProperty("reset_token")
    private String token;
    @JsonProperty("new_password")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String newPassword;
}

package com.madebawojo.nysc.ppa.clearance.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token is required")
    @JsonProperty("refresh_token")
    private String refreshToken;
}


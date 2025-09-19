package com.madebawojo.nysc.ppa.clearance.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetupPasswordRequestDto {

    @NotBlank
    @JsonProperty("setup_token")
    private String setupToken;

    @NotBlank
    private String password;

    @NotBlank
    @JsonProperty("confirm_password")
    private String confirmPassword;
}


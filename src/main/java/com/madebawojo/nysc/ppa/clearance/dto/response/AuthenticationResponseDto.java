package com.madebawojo.nysc.ppa.clearance.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.TokenResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDto {
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String email;
    private Role role;
    @JsonProperty("auth_tokens")
    private TokenResponse authTokens;
//    private String token;
//    private String refreshToken;
//    private Object profile; // You can cast this dynamically (CorperProfileDto, AdminProfileDto, etc.)
}

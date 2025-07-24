package com.madebawojo.nysc.ppa.clearance.dto.response;

import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDto {
    private String token;
    private String email;
    private Role role;
//    private Object profile; // You can cast this dynamically (CorperProfileDto, AdminProfileDto, etc.)
}

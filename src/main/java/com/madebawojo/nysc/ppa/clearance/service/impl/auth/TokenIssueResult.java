package com.madebawojo.nysc.ppa.clearance.service.impl.auth;

import com.madebawojo.nysc.ppa.clearance.entity.auth.RefreshToken;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
@Data
@Builder
public class TokenIssueResult {
    TokenResponse tokenResponse;
    RefreshToken refreshEntity;
}

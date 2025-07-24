package com.madebawojo.nysc.ppa.clearance.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CorperResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String stateCode;
    private String callUpNumber;
    private boolean isActive;
    private String unitName;
    private String ppaName;
}

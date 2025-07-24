package com.madebawojo.nysc.ppa.clearance.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String unitName;
    private String ppaName;
}

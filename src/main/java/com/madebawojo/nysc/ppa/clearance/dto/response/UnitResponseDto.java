package com.madebawojo.nysc.ppa.clearance.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnitResponseDto {
    private Long id;
    private String name;
    private String ppaName;
}

package com.madebawojo.nysc.ppa.clearance.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnitResponseDto {
    private Long id;
    private String name;
    @JsonProperty("ppa_name")
    private String ppaName;
}

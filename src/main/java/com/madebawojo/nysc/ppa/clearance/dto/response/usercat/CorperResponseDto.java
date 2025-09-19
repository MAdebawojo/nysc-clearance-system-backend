package com.madebawojo.nysc.ppa.clearance.dto.response.usercat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CorperResponseDto {
    private Long id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("state_code")
    private String stateCode;
    @JsonProperty("call_up_number")
    private String callUpNumber;
    @JsonProperty("is_active")
    private boolean isActive;
    @JsonProperty("unit_name")
    private String unitName;
    @JsonProperty("ppa_name")
    private String ppaName;
}

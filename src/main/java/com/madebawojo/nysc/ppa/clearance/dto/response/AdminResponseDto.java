package com.madebawojo.nysc.ppa.clearance.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminResponseDto {
    private Long id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String email;
    @JsonProperty("unit_id")
    private String unitName;
    @JsonProperty("ppa_name")
    private String ppaName;
}

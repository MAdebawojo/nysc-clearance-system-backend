package com.madebawojo.nysc.ppa.clearance.dto.request.ppa;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UpdatePpaRequestDto {

    @Size(min = 2, max = 100)
    @JsonProperty("ppa_name")
    private String ppaName;

    @Size(max = 255)
    @JsonProperty("ppa_address")
    private String ppaAddress;

}

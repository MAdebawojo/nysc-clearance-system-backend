package com.madebawojo.nysc.ppa.clearance.dto.request.ppa;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PpaRequestDto {

    @NotBlank(message = "PPA name is required")
    @Size(min = 2, max = 100)
    @JsonProperty("ppa_name")
    private String name;

    @NotBlank(message = "PPA address is required")
    @Size(max = 255)
    @JsonProperty("ppa_address")
    private String address;
}

package com.madebawojo.nysc.ppa.clearance.dto.response.ppa;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PpaResponseDto {
    @JsonProperty("ppa_id")
    private Long id;
    @JsonProperty("ppa_name")
    private String name;
    @JsonProperty("ppa_address")
    private String address;
}

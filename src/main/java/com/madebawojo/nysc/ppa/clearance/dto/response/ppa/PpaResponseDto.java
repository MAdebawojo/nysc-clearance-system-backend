package com.madebawojo.nysc.ppa.clearance.dto.response.ppa;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PpaResponseDto {
    private Long id;
    private String name;
    private String address;
}

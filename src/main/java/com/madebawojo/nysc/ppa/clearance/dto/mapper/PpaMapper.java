package com.madebawojo.nysc.ppa.clearance.dto.mapper;

import com.madebawojo.nysc.ppa.clearance.dto.response.ppa.PpaResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.ppa.Ppa;

public class PpaMapper {
    public static PpaResponseDto toDto(Ppa ppa) {
        if (ppa == null) return null;

        return PpaResponseDto.builder()
                .id(ppa.getId())
                .name(ppa.getName())
                .address(ppa.getAddress())
                .build();
    }
}

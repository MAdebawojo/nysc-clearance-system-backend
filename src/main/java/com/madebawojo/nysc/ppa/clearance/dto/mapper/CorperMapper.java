package com.madebawojo.nysc.ppa.clearance.dto.mapper;

import com.madebawojo.nysc.ppa.clearance.dto.response.CorperResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Corper;

public class CorperMapper {
    public static CorperResponseDto toDto(User user, Corper corper) {
        if (corper == null) return null;

        return CorperResponseDto.builder()
                .id(corper.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .stateCode(corper.getStateCode())
                .callUpNumber(corper.getCallUpNumber())
                .unitName(corper.getUnit().getName())
                .ppaName(corper.getPpa().getName())
                .isActive(corper.isActive())
                .build();
    }
}


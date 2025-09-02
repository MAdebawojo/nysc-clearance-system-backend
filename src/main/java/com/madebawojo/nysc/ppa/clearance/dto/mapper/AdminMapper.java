package com.madebawojo.nysc.ppa.clearance.dto.mapper;

import com.madebawojo.nysc.ppa.clearance.dto.response.AdminResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Admin;

public class AdminMapper {

    public static AdminResponseDto toDto(User user, Admin admin) {
        return AdminResponseDto.builder()
                .id(admin.getId())
                .email(admin.getUser().getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .unitName(admin.getUnit().getName())
                .ppaName(admin.getPpa().getName())
                .build();
    }
}

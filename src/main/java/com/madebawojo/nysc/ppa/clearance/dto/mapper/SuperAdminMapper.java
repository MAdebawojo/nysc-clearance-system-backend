package com.madebawojo.nysc.ppa.clearance.dto.mapper;

import com.madebawojo.nysc.ppa.clearance.dto.response.SuperAdminResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.SuperAdmin;

public class SuperAdminMapper {

    public static SuperAdminResponseDto toDto(User user, SuperAdmin superAdmin) {
        return SuperAdminResponseDto.builder()
                .id(superAdmin.getId())
                .email(superAdmin.getUser().getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .ppaName(superAdmin.getPpa().getName())
                .build();
    }
}


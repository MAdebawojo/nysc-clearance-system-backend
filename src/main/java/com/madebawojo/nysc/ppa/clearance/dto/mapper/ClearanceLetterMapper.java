package com.madebawojo.nysc.ppa.clearance.dto.mapper;

import com.madebawojo.nysc.ppa.clearance.entity.clearance.ClearanceRequest;
import com.madebawojo.nysc.ppa.clearance.entity.ppa.Ppa;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Corper;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.SuperAdmin;
import com.madebawojo.nysc.ppa.clearance.util.ClearanceLetterVariables;

import java.util.Optional;

public final class ClearanceLetterMapper {

    public static ClearanceLetterVariables toVariables(ClearanceRequest request, User user) {
        return ClearanceLetterVariables.builder()
                .clearanceLetterDate(request.getCreatedAt())
                .corpsMemberName(user.getFullName())
                .stateCode(request.getCorper().getStateCode())
                .callUpNumber(request.getCorper().getCallUpNumber())
                .clearanceMonthUpper(request.getClearanceMonth())
//                .signatoryName("Abiodun Ojo")
                .signatoryName(Optional.of(user)
                        .map(User::getCorper)
                        .map(Corper::getPpa)
                        .map(Ppa::getSuperAdmin)
                        .map(SuperAdmin::getUser)
                        .map(User::getFullName)
                        .orElse("Abiodun Ojo")) // Default Name
                .signatoryTitle("Head Human Resources") // Default Department
                .build();
    }
}


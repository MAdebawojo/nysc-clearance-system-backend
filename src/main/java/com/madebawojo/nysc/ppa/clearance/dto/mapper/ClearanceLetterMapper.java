package com.madebawojo.nysc.ppa.clearance.dto.mapper;

import com.madebawojo.nysc.ppa.clearance.entity.clearance.ClearanceRequest;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.util.ClearanceLetterVariables;

public final class ClearanceLetterMapper {

    public static ClearanceLetterVariables toVariables(ClearanceRequest request, User user) {
        return ClearanceLetterVariables.builder()
                .clearanceLetterDate(request.getCreatedAt())
                .corpsMemberName(user.getFullName())
                .stateCode(request.getCorper().getStateCode())
                .callUpNumber(request.getCorper().getCallUpNumber())
                .clearanceMonthUpper(request.getClearanceMonth())
                .signatoryName("Abiodun Ojo")
                .signatoryTitle("Head Human Resources")
                .build();
    }
}


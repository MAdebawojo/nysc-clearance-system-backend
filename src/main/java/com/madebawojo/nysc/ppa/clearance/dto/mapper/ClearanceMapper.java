package com.madebawojo.nysc.ppa.clearance.dto.mapper;

import com.madebawojo.nysc.ppa.clearance.dto.response.clearance.CancelClearanceResponseDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.clearance.ClearanceResponseDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.clearance.RejectClearanceDto;
import com.madebawojo.nysc.ppa.clearance.entity.clearance.ClearanceRequest;

public class ClearanceMapper {
    public static ClearanceResponseDto toDto(ClearanceRequest request) {
        if (request == null) return null;

        return ClearanceResponseDto.builder()
                .id(request.getId())
                .corperId(request.getCorper().getId())
                .corperName(request.getCorper().getUser().getFullName())
                .clearanceMonth(request.getClearanceMonth())
                .tentativeDate(request.getTentativeDate())
                .status(request.getStatus())
                .rejectionReason(request.getRejectionReason())
                .build();
    }

    public static CancelClearanceResponseDto cancelResponseDto(ClearanceRequest request) {
        if (request == null) return null;

        return CancelClearanceResponseDto.builder()
                .id(request.getId())
                .corperId(request.getCorper().getId())
                .corperName(request.getCorper().getUser().getFullName())
                .clearanceMonth(request.getClearanceMonth())
                .tentativeDate(request.getTentativeDate())
                .status(request.getStatus())
//                .cancellationReason(request.getCancellationReason())
                .build();
    }

    public static RejectClearanceDto rejectResponseDto(ClearanceRequest request) {
        if (request == null) return null;

        return RejectClearanceDto.builder()
                .id(request.getId())
                .corperId(request.getCorper().getId())
                .corperName(request.getCorper().getUser().getFullName())
                .clearanceMonth(request.getClearanceMonth())
                .tentativeDate(request.getTentativeDate())
                .status(request.getStatus())
                .rejectionReason(request.getRejectionReason())
                .build();
    }
}

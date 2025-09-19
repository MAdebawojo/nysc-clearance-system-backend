package com.madebawojo.nysc.ppa.clearance.service.servicecontract.clearance;

import com.madebawojo.nysc.ppa.clearance.dto.request.clearance.ClearanceRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.clearance.CancelClearanceResponseDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.clearance.ClearanceResponseDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.clearance.RejectClearanceDto;

import java.util.List;

public interface ClearanceService {
    // Corper actions
    ClearanceResponseDto createRequest(Long corperId, ClearanceRequestDto dto);
    CancelClearanceResponseDto cancelRequest(Long corperId, Long requestId, String reason);
//    ClearanceRequest resubmitRequest(Long corperId, Long requestId, ClearanceRequestDto dto);

    // Unit Head actions
    ClearanceResponseDto approveByUnitHead(Long requestId, Long unitHeadId);
    RejectClearanceDto rejectByUnitHead(Long requestId, Long unitHeadId, String reason);

    // Super Admin actions
    ClearanceResponseDto approveBySuperAdmin(Long requestId, Long superAdminId);
    RejectClearanceDto rejectBySuperAdmin(Long requestId, Long superAdminId, String reason);

    // Views
    List<ClearanceResponseDto> getRequestsForCorper(Long corperId);
    List<ClearanceResponseDto> getRequestsForUnit(Long unitHeadId);
    List<ClearanceResponseDto> getAllRequestsForPpa(Long ppaId);

    byte[] downloadClearancePDF(Long requestId);
}

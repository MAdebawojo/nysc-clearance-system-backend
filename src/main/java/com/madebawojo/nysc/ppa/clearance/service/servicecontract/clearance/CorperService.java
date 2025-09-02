package com.madebawojo.nysc.ppa.clearance.service.servicecontract.clearance;

import com.madebawojo.nysc.ppa.clearance.dto.request.CorperRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.UpdateCredentialsRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.CorperResponseDto;

import java.util.List;

public interface CorperService {
    CorperResponseDto createCorper(CorperRequestDto dto);
    CorperResponseDto getCorperById(Long userId);
    List<CorperResponseDto> getAllCorpersInUnit(Long unitId);
    List<CorperResponseDto> getAllCorpersInPpa(Long ppaId);
    CorperResponseDto updateCorper(Long UserId, CorperRequestDto dto);
    void updateCredentials(String email, UpdateCredentialsRequestDto request);
    void deleteCorper(Long userId);
    void blockCorper(Long id);
    void unblockCorper(Long id);
}

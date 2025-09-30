package com.madebawojo.nysc.ppa.clearance.service.servicecontract.usercat;

import com.madebawojo.nysc.ppa.clearance.dto.request.usercat.CorperRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.auth.UpdateCredentialsRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.usercat.CorperResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Corper;

import java.util.List;

public interface CorperService {
    CorperResponseDto createCorper(Long superAdminId, CorperRequestDto dto);
    CorperResponseDto getCorperById(Long userId);
    Corper getCorperEntityById(Long corperId);
    List<CorperResponseDto> getAllCorpersInUnit(Long unitId);
    List<CorperResponseDto> getAllCorpersInPpa(Long ppaId);
    CorperResponseDto updateCorper(Long UserId, CorperRequestDto dto);
    void updateCredentials(String email, UpdateCredentialsRequestDto request);
    void deleteCorper(Long userId);
    void blockCorper(Long id);
    void unblockCorper(Long id);
}

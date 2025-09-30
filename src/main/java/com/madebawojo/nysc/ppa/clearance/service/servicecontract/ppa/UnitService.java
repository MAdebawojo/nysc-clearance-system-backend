package com.madebawojo.nysc.ppa.clearance.service.servicecontract.ppa;

import com.madebawojo.nysc.ppa.clearance.dto.request.ppa.UnitRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.ppa.UnitResponseDto;

import java.util.List;

public interface UnitService {
    UnitResponseDto createUnitBySuperAdmin(Long userId, UnitRequestDto dto);
    List<UnitResponseDto> getAllUnitsInPpaById(Long ppaId);
    List<UnitResponseDto> getSuperAdminUnits(Long userId);
    UnitResponseDto getUnitById(Long id);
    UnitResponseDto updateUnit(Long id, UnitRequestDto dto);
    void deleteUnit(Long id);
    boolean isHeadOfCorperUnit(Long unitHeadId, Long corperId);
}

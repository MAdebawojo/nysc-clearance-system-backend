package com.madebawojo.nysc.ppa.clearance.service.servicecontract.clearance;

import com.madebawojo.nysc.ppa.clearance.dto.request.UnitRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.UnitResponseDto;

import java.util.List;

public interface UnitService {
    UnitResponseDto createUnit(UnitRequestDto dto);
    List<UnitResponseDto> getAllUnitsInPpa(Long ppaId);
    UnitResponseDto getUnitById(Long id);
    UnitResponseDto updateUnit(Long id, UnitRequestDto dto);
    void deleteUnit(Long id);
}

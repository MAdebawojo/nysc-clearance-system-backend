package com.madebawojo.nysc.ppa.clearance.dto.mapper;

import com.madebawojo.nysc.ppa.clearance.dto.request.UnitRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.UnitResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.Unit;
import com.madebawojo.nysc.ppa.clearance.entity.Ppa;

public class UnitMapper {

    public static UnitResponseDto toDto(Unit unit) {
        if (unit == null) return null;

        return UnitResponseDto.builder()
                .id(unit.getId())
                .name(unit.getName())
                .ppaName(unit.getPpa() != null ? unit.getPpa().getName() : null)
                .build();
    }

    public static Unit toEntity(UnitRequestDto dto, Ppa ppa) {
        return Unit.builder()
                .name(dto.getName())
                .ppa(ppa)
                .build();
    }

    public static void updateEntity(Unit unit, UnitRequestDto dto, Ppa ppa) {
        unit.setName(dto.getName());
        unit.setPpa(ppa);
    }
}

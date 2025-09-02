package com.madebawojo.nysc.ppa.clearance.service.impl.clearance;

import com.madebawojo.nysc.ppa.clearance.core.exception.ResourceNotFoundException;
import com.madebawojo.nysc.ppa.clearance.dto.mapper.UnitMapper;
import com.madebawojo.nysc.ppa.clearance.dto.request.UnitRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.UnitResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.Ppa;
import com.madebawojo.nysc.ppa.clearance.entity.Unit;
import com.madebawojo.nysc.ppa.clearance.repository.PpaRepository;
import com.madebawojo.nysc.ppa.clearance.repository.UnitRepository;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.clearance.UnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final PpaRepository ppaRepository;

    @Override
    public UnitResponseDto createUnit(UnitRequestDto dto) {
        Ppa ppa = ppaRepository.findById(dto.getPpaId())
                .orElseThrow(() -> new ResourceNotFoundException("PPA not found"));

        Unit unit = Unit.builder()
                .name(dto.getName())
                .ppa(ppa)
                .build();

        unitRepository.save(unit);
        log.info("Created unit '{}' under PPA '{}'", unit.getName(), ppa.getName());

        return UnitMapper.toDto(unit);
    }

    @Override
    public List<UnitResponseDto> getAllUnitsInPpa(Long ppaId) {
        Ppa ppa = ppaRepository.findById(ppaId)
                .orElseThrow(() -> new ResourceNotFoundException("PPA not found"));

        return unitRepository.findAllByPpa(ppa).stream()
                .map(UnitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UnitResponseDto getUnitById(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found"));

        log.info("Retrieved unit: {}", unit.getName());
        return UnitMapper.toDto(unit);
    }

    @Override
    public UnitResponseDto updateUnit(Long id, UnitRequestDto dto) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found"));
        unit.setName(dto.getName());
        unitRepository.save(unit);
        log.info("Updated unit: {}", unit.getName());
        return UnitMapper.toDto(unit);
    }

    @Override
    public void deleteUnit(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found"));
        unitRepository.delete(unit);
        log.info("Deleted unit with ID: {}", id);
    }
}

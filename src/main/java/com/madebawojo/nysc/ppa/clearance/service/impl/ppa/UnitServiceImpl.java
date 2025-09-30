package com.madebawojo.nysc.ppa.clearance.service.impl.ppa;

import com.madebawojo.nysc.ppa.clearance.core.exception.ResourceNotFoundException;
import com.madebawojo.nysc.ppa.clearance.dto.mapper.UnitMapper;
import com.madebawojo.nysc.ppa.clearance.dto.request.ppa.UnitRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.ppa.UnitResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.ppa.Ppa;
import com.madebawojo.nysc.ppa.clearance.entity.ppa.Unit;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Admin;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Corper;
import com.madebawojo.nysc.ppa.clearance.repository.AdminRepository;
import com.madebawojo.nysc.ppa.clearance.repository.CorperRepository;
import com.madebawojo.nysc.ppa.clearance.repository.PpaRepository;
import com.madebawojo.nysc.ppa.clearance.repository.UnitRepository;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.AuthServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.impl.usercat.SuperAdminServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.ppa.UnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final PpaRepository ppaRepository;
    private final AdminRepository adminRepository;
    private final CorperRepository corperRepository;

    private final SuperAdminServiceImpl superAdminService;
    private final AuthServiceImpl authService;

    @Override
    public UnitResponseDto createUnitBySuperAdmin(Long userId, UnitRequestDto dto) {
//        Ppa ppa = ppaRepository.findById(dto.getPpaId())
//                .orElseThrow(() -> new ResourceNotFoundException("PPA not found"));
        Ppa ppa = superAdminService.getSuperAdminEntityById(userId).getPpa();

        Unit unit = Unit.builder()
                .name(dto.getName())
                .ppa(ppa)
                .build();

        unitRepository.save(unit);
        log.info("Created unit '{}' under PPA '{}'", unit.getName(), ppa.getName());

        return UnitMapper.toDto(unit);
    }

    @Override
    public List<UnitResponseDto> getAllUnitsInPpaById(Long ppaId) {
        log.info("Fetching all Units for PPA with id: {}", ppaId);

        Ppa ppa = ppaRepository.findById(ppaId)
                .orElseThrow(() -> {
                    log.error("PPA not found with id: {}", ppaId);
                    return new ResourceNotFoundException("PPA not found");
                });

        List<UnitResponseDto> units = unitRepository.findAllByPpa(ppa).stream()
                .map(UnitMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} unit(s) for PPA with id: {}", units.size(), ppaId);

        return units;
    }

    @Override
    public List<UnitResponseDto> getSuperAdminUnits(Long userId) {
        log.info("Fetching Units for SuperAdmin with userId: {}", userId);

        Ppa ppa = superAdminService.getSuperAdminEntityById(userId).getPpa();

        if (ppa == null) {
            log.warn("SuperAdmin with userId: {} has no assigned PPA", userId);
            return Collections.emptyList();
        }

        List<UnitResponseDto> units = unitRepository.findAllByPpa(ppa).stream()
                .map(UnitMapper::toDto)
                .collect(Collectors.toList());

        log.info("SuperAdmin with userId: {} has {} unit(s) in PPA id: {}",
                userId, units.size(), ppa.getId());

        return units;
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

    @Override
    public boolean isHeadOfCorperUnit(Long unitHeadId, Long corperId) {
        Admin unitHead = adminRepository.findById(unitHeadId)
                .orElseThrow(() -> {
                    log.error("Admin with ID {} not found (isHeadOfCorperUnit)", unitHeadId);
                    return new ResourceNotFoundException("Admin not found with id: " + unitHeadId);
                });

        Corper corper = corperRepository.findById(corperId)
                .orElseThrow(() -> {
                    log.error("Corper with ID {} not found (isHeadOfCorperUnit)", corperId);
                    return new ResourceNotFoundException("Corper not found with id: " + corperId);
                });

        boolean isHead = unitHead.getUnit() != null
                && corper.getUnit() != null
                && unitHead.getUnit().getId().equals(corper.getUnit().getId());

        log.debug("Checking if Admin {} is head of Corper {}'s unit: {}", unitHeadId, corperId, isHead);
        return isHead;
    }

}

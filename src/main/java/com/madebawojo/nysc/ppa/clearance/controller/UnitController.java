package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.request.UnitRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.UnitResponseDto;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.clearance.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponseStructure<UnitResponseDto>> createUnit(@Valid @RequestBody UnitRequestDto dto) {
        log.info("Creating new unit");

        UnitResponseDto createdUnit = unitService.createUnit(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUnit.getId()).toUri();

        log.info("Location header for created unit: {}", location);
        return ResponseEntity
                .created(location)
                .body(ApiResponseStructure.success("Unit created successfully", createdUnit, HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<UnitResponseDto>> getUnitById(@PathVariable Long id) {
        UnitResponseDto unit = unitService.getUnitById(id);
        return ResponseEntity.ok(ApiResponseStructure.success("Unit retrieved", unit, HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/ppa/{ppaId}")
    public ResponseEntity<ApiResponseStructure<List<UnitResponseDto>>> getUnitsByPpa(@PathVariable Long ppaId) {
        List<UnitResponseDto> units = unitService.getAllUnitsInPpa(ppaId);
        return ResponseEntity.ok(ApiResponseStructure.success("Units retrieved by PPA", units, HttpStatus.OK.value()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<UnitResponseDto>> updateUnit(@PathVariable Long id,
                                                                            @RequestBody UnitRequestDto dto) {
        UnitResponseDto updated = unitService.updateUnit(id, dto);
        return ResponseEntity.ok(ApiResponseStructure.success("Unit updated successfully", updated, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<?>> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.ok(ApiResponseStructure.success("Unit deleted successfully", null, HttpStatus.OK.value()));
    }
}

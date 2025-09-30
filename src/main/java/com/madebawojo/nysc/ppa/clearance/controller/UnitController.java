package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.request.ppa.UnitRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.ppa.UnitResponseDto;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.ppa.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
@Tag(name = "Units", description = "Manage Units within PPAs. Restricted to Super Admins.")
@SecurityRequirement(name = "bearerAuth")
public class UnitController {

    private final UnitService unitService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Create a new Unit",
            description = "Creates a new Unit under a PPA. Only SUPER_ADMIN can perform this action.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Unit created successfully",
                            content = @Content(schema = @Schema(implementation = UnitResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – insufficient permissions")
            }
    )
    public ResponseEntity<ApiResponseStructure<UnitResponseDto>> createUnitBySuperAdmin(@AuthenticationPrincipal(expression = "id") Long userId, @Valid @RequestBody UnitRequestDto dto) {
        log.info("Creating new unit");

        UnitResponseDto createdUnit = unitService.createUnitBySuperAdmin(userId, dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUnit.getId()).toUri();

        log.info("Location header for created unit: {}", location);
        return ResponseEntity
                .created(location)
                .body(ApiResponseStructure.success("Unit created successfully", createdUnit, HttpStatus.CREATED.value()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Get Unit by ID",
            description = "Retrieves details of a Unit by its ID. Requires SUPER_ADMIN role."
    )
    public ResponseEntity<ApiResponseStructure<UnitResponseDto>> getUnitById(@PathVariable Long id) {
        UnitResponseDto unit = unitService.getUnitById(id);
        return ResponseEntity.ok(ApiResponseStructure.success("Unit retrieved", unit, HttpStatus.OK.value()));
    }

    @GetMapping("/ppa/{ppaId}")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @Operation(
            summary = "Get all Units in a PPA",
            description = "Retrieves all Units belonging to a specific PPA. Requires GLOBAL_ADMIN role."
    )
    public ResponseEntity<ApiResponseStructure<List<UnitResponseDto>>> getUnitsByPpa(@PathVariable Long ppaId) {
        List<UnitResponseDto> units = unitService.getAllUnitsInPpaById(ppaId);
        return ResponseEntity.ok(ApiResponseStructure.success("Units retrieved by PPA", units, HttpStatus.OK.value()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Get all Units in a PPA",
            description = "Retrieves all Units belonging to PPA of an Authenticated Super-Admin. Requires SUPER_ADMIN role."
    )
    public ResponseEntity<ApiResponseStructure<List<UnitResponseDto>>> getUnitsInPpaBySuperAdmin(@AuthenticationPrincipal(expression = "id") Long userId) {

        List<UnitResponseDto> units = unitService.getSuperAdminUnits(userId);
        return ResponseEntity.ok(ApiResponseStructure.success("Units retrieved in PPA", units, HttpStatus.OK.value()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Update a Unit",
            description = "Updates details of an existing Unit. Requires SUPER_ADMIN role."
    )
    public ResponseEntity<ApiResponseStructure<UnitResponseDto>> updateUnit(@PathVariable Long id,
                                                                            @RequestBody UnitRequestDto dto) {
        UnitResponseDto updated = unitService.updateUnit(id, dto);
        return ResponseEntity.ok(ApiResponseStructure.success("Unit updated successfully", updated, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Delete a Unit",
            description = "Deletes a Unit by ID. Requires SUPER_ADMIN role."
    )
    public ResponseEntity<ApiResponseStructure<?>> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.ok(ApiResponseStructure.success("Unit deleted successfully", null, HttpStatus.OK.value()));
    }
}

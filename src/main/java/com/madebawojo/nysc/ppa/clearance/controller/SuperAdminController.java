package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.request.SuperAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.UpdateSuperAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.response.SuperAdminResponseDto;
import com.madebawojo.nysc.ppa.clearance.service.impl.SuperAdminServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/super-admin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final SuperAdminServiceImpl superAdminService;

    @PostMapping
    public ResponseEntity<ApiResponseStructure<SuperAdminResponseDto>> createSuperAdmin(@Valid @RequestBody SuperAdminRequestDto request) {
        log.info("Creating new Super Admin");
        SuperAdminResponseDto createdSuperAdmin = superAdminService.createSuperAdmin(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdSuperAdmin.getId()).toUri();

        log.info("Location header for created Super Admin: {}", location);
        return ResponseEntity
                .created(location)
                .body(ApiResponseStructure.success("Super Admin created successfully", createdSuperAdmin, HttpStatus.CREATED.value()));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<SuperAdminResponseDto>> getSuperAdminById(@PathVariable Long id) {
        log.info("Fetching Super Admin with ID: {}", id);
        SuperAdminResponseDto response = superAdminService.getSuperAdminProfileById(id);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Super Admin fetched successfully", response, HttpStatus.OK.value())
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponseStructure<List<SuperAdminResponseDto>>> getAllSuperAdmins() {
        log.info("Fetching all Super Admins");
        List<SuperAdminResponseDto> responseList = superAdminService.getAllSuperAdminProfiles();
        return ResponseEntity.ok(
                ApiResponseStructure.success("Super Admins fetched successfully", responseList, HttpStatus.OK.value())
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<SuperAdminResponseDto>> updateSuperAdmin(@PathVariable Long id,
                                                                                   @Valid @RequestBody UpdateSuperAdminRequestDto request) {
        log.info("Updating Super Admin with ID: {}", id);
        SuperAdminResponseDto response = superAdminService.updateSuperAdminProfile(id, request);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Super Admin updated successfully", response, HttpStatus.OK.value())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<String>> deleteSuperAdmin(@PathVariable Long id) {
        log.warn("Request to delete Super Admin with ID: {}", id);
        superAdminService.deleteSuperAdmin(id);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Super Admin deleted successfully", null, HttpStatus.OK.value())
        );
    }
}

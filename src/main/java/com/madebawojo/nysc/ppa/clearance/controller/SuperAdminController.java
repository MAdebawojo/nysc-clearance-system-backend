package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.request.SuperAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.UpdateSuperAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.response.SuperAdminResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.service.impl.clearance.SuperAdminServiceImpl;
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
@RequestMapping("/api/v1/super-admin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final SuperAdminServiceImpl superAdminService;

    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponseStructure<SuperAdminResponseDto>> createSuperAdmin(@Valid @RequestBody SuperAdminRequestDto request) {
        log.info("######################GLOBAL ADMIN ACTION!!!###########################");
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

    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<SuperAdminResponseDto>> getSuperAdminById(@PathVariable Long id) {
        log.info("Fetching Super Admin with ID: {}", id);
        SuperAdminResponseDto response = superAdminService.getSuperAdminProfileById(id);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Super Admin fetched successfully", response, HttpStatus.OK.value())
        );
    }

    // Get Authenticated Super Admin
    @GetMapping("/me")
    public ResponseEntity<ApiResponseStructure<SuperAdminResponseDto>> retrieveAuthenticatedSuperAdmin(@AuthenticationPrincipal(expression = "id") User user) {
        SuperAdminResponseDto response = superAdminService.getSuperAdminProfileById(user.getId());
        return ResponseEntity.ok(ApiResponseStructure.success("Corper retrieved successfully", response, HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponseStructure<List<SuperAdminResponseDto>>> getAllSuperAdmins() {
        log.info("Fetching all Super Admins");
        List<SuperAdminResponseDto> responseList = superAdminService.getAllSuperAdminProfiles();
        return ResponseEntity.ok(
                ApiResponseStructure.success("Super Admins fetched successfully", responseList, HttpStatus.OK.value())
        );
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<SuperAdminResponseDto>> updateSuperAdmin(@PathVariable Long id,
                                                                                   @Valid @RequestBody UpdateSuperAdminRequestDto request) {
        log.info("Updating Super Admin with ID: {}", id);
        SuperAdminResponseDto response = superAdminService.updateSuperAdminProfile(id, request);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Super Admin updated successfully", response, HttpStatus.OK.value())
        );
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/me")
    public ResponseEntity<ApiResponseStructure<SuperAdminResponseDto>> updateAuthenticatedSuperAdmin(@AuthenticationPrincipal(expression = "id") User user,
                                                                                        @Valid @RequestBody UpdateSuperAdminRequestDto request) {
        log.info("Updating authenticated Super Admin with ID: {}", user.getId());
        SuperAdminResponseDto response = superAdminService.updateSuperAdminProfile(user.getId(), request);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Super Admin updated successfully", response, HttpStatus.OK.value())
        );
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<String>> deleteSuperAdmin(@PathVariable Long id) {
        log.warn("Request to delete Super Admin with ID: {}", id);
        superAdminService.deleteSuperAdmin(id);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Super Admin deleted successfully", null, HttpStatus.OK.value())
        );
    }
}

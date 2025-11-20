package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.request.usercat.SuperAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.usercat.UpdateSuperAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.response.usercat.SuperAdminResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.service.impl.usercat.SuperAdminServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/super-admin")
@RequiredArgsConstructor
@Tag(name = "Super Admins", description = "Endpoints for managing Super Admin accounts. Some actions are restricted to Global Admins.")
@SecurityRequirement(name = "bearerAuth")
public class SuperAdminController {

    private final SuperAdminServiceImpl superAdminService;

    @Operation(
            summary = "Create a new Super Admin",
            description = "Accessible only by GLOBAL_ADMIN. Creates a new Super Admin and sends an email verification link.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Super Admin created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - requires GLOBAL_ADMIN role")
            }
    )
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponseStructure<SuperAdminResponseDto>> createSuperAdmin(@Valid @RequestBody SuperAdminRequestDto request) {
        log.info("######################GLOBAL ADMIN ACTION!!!###########################");
        log.info("Creating new Super Admin");
        SuperAdminResponseDto createdSuperAdmin = superAdminService.createSuperAdmin(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdSuperAdmin.getId()).toUri();

        log.info("Super Admin successfully created. Location header for created Super Admin: {}", location);
        return ResponseEntity
                .created(location)
                .body(ApiResponseStructure.success("Email verification link has been sent to the registered account.", createdSuperAdmin, HttpStatus.CREATED.value()));

    }

    @Operation(
            summary = "Get authenticated Super Admin",
            description = "Fetches the profile of the authenticated Super Admin. Requires GLOBAL_ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Super Admin fetched successfully"),
                    @ApiResponse(responseCode = "404", description = "Super Admin not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - requires GLOBAL_ADMIN role")
            }
    )
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<SuperAdminResponseDto>> getSuperAdminById(@PathVariable Long id) {
        log.info("Fetching Super Admin with ID: {}", id);
        SuperAdminResponseDto response = superAdminService.getSuperAdminProfileById(id);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Super Admin fetched successfully", response, HttpStatus.OK.value())
        );
    }

    @Operation(
            summary = "Get Super Admin by ID",
            description = "Fetches the profile of a Super Admin by ID. Requires GLOBAL_ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Super Admin fetched successfully"),
                    @ApiResponse(responseCode = "404", description = "Super Admin not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - requires GLOBAL_ADMIN role")
            }
    )
    // Get Authenticated Super Admin
    @GetMapping("/me")
    public ResponseEntity<ApiResponseStructure<SuperAdminResponseDto>> retrieveAuthenticatedSuperAdmin(@AuthenticationPrincipal(expression = "id") User user) {
        SuperAdminResponseDto response = superAdminService.getSuperAdminProfileById(user.getId());
        return ResponseEntity.ok(ApiResponseStructure.success("Super Admin retrieved successfully", response, HttpStatus.OK.value()));
    }

//    @PreAuthorize("hasRole('SUPER_ADMIN')")
//    @GetMapping
//    public ResponseEntity<ApiResponseStructure<List<SuperAdminResponseDto>>> getAllSuperAdmins() {
//        log.info("Fetching all Super Admins");
//        List<SuperAdminResponseDto> responseList = superAdminService.getAllSuperAdminProfiles();
//        return ResponseEntity.ok(
//                ApiResponseStructure.success("Super Admins fetched successfully", responseList, HttpStatus.OK.value())
//        );
//    }

    @Operation(
            summary = "Update Super Admin profile by user ID {id}",
            description = "Updates the profile of a Super Admin by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Super Admin updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - not logged in")
            }
    )
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

    @Operation(
            summary = "Update authenticated Super Admin profile",
            description = "Updates the profile of the currently authenticated Super Admin.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Super Admin updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - not logged in")
            }
    )
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

    @Operation(
            summary = "Delete a Super Admin",
            description = "Deletes a Super Admin account by ID. Requires SUPER_ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Super Admin deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - requires SUPER_ADMIN role"),
                    @ApiResponse(responseCode = "404", description = "Super Admin not found")
            }
    )
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

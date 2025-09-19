package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.request.usercat.AdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.usercat.UpdateAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.usercat.AdminResponseDto;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.usercat.AdminService;
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

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
@Tag(name = "Admins", description = "Manage Admin accounts and profiles. Some actions restricted to Super Admins.")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Create a new Admin",
            description = "Creates a new admin. Only SUPER_ADMIN can perform this action. " +
                    "A verification email is sent to the newly created admin/unit-head.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Admin created successfully",
                            content = @Content(schema = @Schema(implementation = AdminResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request body"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – insufficient permissions")
            }
    )
    public ResponseEntity<ApiResponseStructure<AdminResponseDto>> createAdmin(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody AdminRequestDto request) {

        log.info("Creating new admin with email: {}", request.getEmail());

        AdminResponseDto response = adminService.createAdmin(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseStructure.success("Admin created successfully. Email verification link has been sent to the registered account.", response, HttpStatus.CREATED.value())
        );
    }

    @GetMapping("/{adminId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Get Admin Profile by ID",
            description = "Retrieve the details of an Admin by their ID. Requires SUPER_ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Admin retrieved successfully",
                            content = @Content(schema = @Schema(implementation = AdminResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Admin not found")
            }
    )
    public ResponseEntity<ApiResponseStructure<AdminResponseDto>> getAdminProfile(@PathVariable Long userId) {
        log.info("Fetching admin profile for user ID: {}", userId);
        AdminResponseDto response = adminService.getAdminById(userId);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Admin profile retrieved successfully", response, HttpStatus.OK.value())
        );
    }

    // Get Admin Profile (Authenticated Admin)
    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get Authenticated Admin Profile",
            description = "Retrieves the profile of the currently authenticated Admin. Requires ADMIN role."
    )
    public ResponseEntity<ApiResponseStructure<AdminResponseDto>> retrieveAuthenticatedAdmin(
            @AuthenticationPrincipal(expression = "id") Long adminId) {
        log.info("Fetching profile for authenticated admin ID: {}", adminId);
        AdminResponseDto response = adminService.getAdminById(adminId);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Admin profile retrieved successfully", response, HttpStatus.OK.value())
        );
    }



    @GetMapping("/{adminId}/unit-id")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Get Unit ID of an Admin",
            description = "Retrieves the Unit ID associated with a given Admin. Requires SUPER_ADMIN role."
    )
    public ResponseEntity<ApiResponseStructure<Long>> getUnitIdByAdmin(@PathVariable Long userId) {
        log.info("Fetching unit ID for admin with user ID: {}", userId);
        Long unitId = adminService.getUnitIdByAdmin(userId);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Unit ID retrieved successfully", unitId, HttpStatus.OK.value())
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Get All Admins",
            description = "Retrieves a list of all Admins. Requires SUPER_ADMIN role."
    )
    public ResponseEntity<ApiResponseStructure<List<AdminResponseDto>>> getAllAdmins() {
        log.info("Fetching all admins");
        List<AdminResponseDto> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(
                ApiResponseStructure.success("All admins retrieved successfully", admins, HttpStatus.OK.value())
        );
    }

    @PutMapping("/{adminId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Update an Admin",
            description = "Updates the profile of an Admin by ID. Requires SUPER_ADMIN role."
    )
    public ResponseEntity<ApiResponseStructure<AdminResponseDto>> updateAdmin(
            @PathVariable Long adminId,
            @Valid @RequestBody UpdateAdminRequestDto request) {
        log.info("Updating admin with ID: {}", adminId);
        AdminResponseDto updatedAdmin = adminService.updateAdmin(adminId, request);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Admin updated successfully", updatedAdmin, HttpStatus.OK.value())
        );
    }

    // Get Admin Profile (Authenticated Admin)
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/me")
//    public ResponseEntity<ApiResponseStructure<AdminResponseDto>> updateAuthenticatedAdmin(
//            @AuthenticationPrincipal(expression = "id") Long adminId,
//            @Valid @RequestBody UpdateAdminRequestDto request) {
//
//        log.info("Updating admin with ID: {}", adminId);
//        AdminResponseDto updatedAdmin = adminService.updateAdmin(adminId, request);
//        return ResponseEntity.ok(
//                ApiResponseStructure.success("Admin updated successfully", updatedAdmin, HttpStatus.OK.value())
//        );
//    }

    @DeleteMapping("/{adminId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Delete an Admin",
            description = "Deletes an Admin by ID. Requires SUPER_ADMIN role."
    )
    public ResponseEntity<ApiResponseStructure<Void>> deleteAdmin(@PathVariable Long adminId) {
        log.info("Deleting admin with ID: {}", adminId);
        adminService.deleteAdmin(adminId);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Admin deleted successfully", null, HttpStatus.OK.value())
        );
    }
}





//@RestController
//@RequestMapping("/api/v1/admin")
//@RequiredArgsConstructor
//public class AdminController {
//
//    private final AdminService adminService;
//
//    @GetMapping("/profile")
//    public ResponseEntity<ApiResponseStructure<AdminResponseDto>> getAdminProfile(
//            @AuthenticationPrincipal(expression = "id") Long userId) {
//        AdminResponseDto profile = adminService.getAdminProfile(userId);
//        return ResponseEntity.ok(
//                ApiResponseStructure.success("Admin profile retrieved successfully", profile, HttpStatus.OK.value())
//        );
//    }
//
//}



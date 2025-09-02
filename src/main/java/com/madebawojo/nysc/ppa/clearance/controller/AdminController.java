package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.request.AdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.UpdateAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.AdminResponseDto;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.clearance.AdminService;
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
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponseStructure<AdminResponseDto>> createAdmin(
            @Valid @RequestBody AdminRequestDto request) {
        log.info("Creating new admin with email: {}", request.getEmail());
        AdminResponseDto response = adminService.createAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseStructure.success("Admin created successfully", response, HttpStatus.CREATED.value())
        );
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/{adminId}")
    public ResponseEntity<ApiResponseStructure<AdminResponseDto>> getAdminProfile(@PathVariable Long userId) {
        log.info("Fetching admin profile for user ID: {}", userId);
        AdminResponseDto response = adminService.getAdminById(userId);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Admin profile retrieved successfully", response, HttpStatus.OK.value())
        );
    }

    // Get Admin Profile (Authenticated Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponseStructure<AdminResponseDto>> retrieveAuthenticatedAdmin(
            @AuthenticationPrincipal(expression = "id") Long adminId) {
        log.info("Fetching profile for authenticated admin ID: {}", adminId);
        AdminResponseDto response = adminService.getAdminById(adminId);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Admin profile retrieved successfully", response, HttpStatus.OK.value())
        );
    }



    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/{adminId}/unit-id")
    public ResponseEntity<ApiResponseStructure<Long>> getUnitIdByAdmin(@PathVariable Long userId) {
        log.info("Fetching unit ID for admin with user ID: {}", userId);
        Long unitId = adminService.getUnitIdByAdmin(userId);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Unit ID retrieved successfully", unitId, HttpStatus.OK.value())
        );
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponseStructure<List<AdminResponseDto>>> getAllAdmins() {
        log.info("Fetching all admins");
        List<AdminResponseDto> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(
                ApiResponseStructure.success("All admins retrieved successfully", admins, HttpStatus.OK.value())
        );
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{adminId}")
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

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{adminId}")
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



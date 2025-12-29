package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.request.usercat.CorperRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.usercat.CorperResponseDto;
import com.madebawojo.nysc.ppa.clearance.service.impl.usercat.CorperServiceImpl;
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
@RequestMapping("/api/v1/corpers")
@RequiredArgsConstructor
@Tag(name = "Corp Members", description = "Manage Corper accounts, profiles, and assignments.")
@SecurityRequirement(name = "bearerAuth")
public class CorperController {

    private final CorperServiceImpl corperService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Create a new Corper",
            description = "Registers a new Corper. Only ADMIN and SUPER_ADMIN roles can perform this.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Corper created successfully",
                            content = @Content(schema = @Schema(implementation = CorperResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – insufficient permissions")
            }
    )
    public ResponseEntity<ApiResponseStructure<CorperResponseDto>> createCorper(@AuthenticationPrincipal (expression = "id") Long superAdminId, @Valid @RequestBody CorperRequestDto dto) {
        CorperResponseDto response = corperService.createCorper(superAdminId, dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.getId()).toUri();

        log.info("Location header for created corper: {}", location);

        return ResponseEntity
                .created(location)
                .body(ApiResponseStructure.success("Corper created successfully. Email verification link has been sent to the registered account.", response, HttpStatus.CREATED.value()));
    }

    // Get Corper Profile (Authenticated Corper)
    @GetMapping("/me")
    @Operation(summary = "Get authenticated Corper profile")
    public ResponseEntity<ApiResponseStructure<CorperResponseDto>> retrieveAuthenticatedCorper(@AuthenticationPrincipal(expression = "id") Long userId) {
        CorperResponseDto response = corperService.getCorperById(userId);
        return ResponseEntity.ok(ApiResponseStructure.success("Corper retrieved successfully", response, HttpStatus.OK.value()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Retrieve Corper by ID (Admin access)")
    public ResponseEntity<ApiResponseStructure<CorperResponseDto>> retrieveCorperWithIdByAdmin(Long userId) {
        CorperResponseDto response = corperService.getCorperById(userId);
        return ResponseEntity.ok(ApiResponseStructure.success("Corper retrieved successfully", response, HttpStatus.OK.value()));
    }

    // Update Corper Profile
    @PutMapping("/me")
    @Operation(summary = "Update authenticated Corper profile")
    public ResponseEntity<ApiResponseStructure<CorperResponseDto>> updateCorper(@AuthenticationPrincipal(expression = "id") Long userId,
                                                                                @Valid @RequestBody CorperRequestDto dto) {
        CorperResponseDto response = corperService.updateCorper(userId, dto);
        return ResponseEntity.ok(ApiResponseStructure.success("Corper updated successfully", response, HttpStatus.OK.value()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Update Corper by ID (Admin access)")
    public ResponseEntity<ApiResponseStructure<CorperResponseDto>> updateCorperWithIdByAdmin(Long userId, @Valid @RequestBody CorperRequestDto dto) {
        CorperResponseDto response = corperService.updateCorper(userId, dto);
        return ResponseEntity.ok(ApiResponseStructure.success("Corper updated successfully", response, HttpStatus.OK.value()));
    }

    // Update Email/Password
//    @PutMapping("/me/credentials")
//    public ResponseEntity<ApiResponseStructure<String>> updateCredentials(@AuthenticationPrincipal(expression = "email") String email,
//                                                                          @Valid @RequestBody UpdateCredentialsRequestDto request) {
//        corperService.updateCredentials(email, request);
//        return ResponseEntity.ok(ApiResponseStructure.success("Credentials updated successfully", null, HttpStatus.NO_CONTENT.value()));
//    }

    @GetMapping("/unit/{unitId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get all Corpers in a Unit (Admin access)")
    public ResponseEntity<ApiResponseStructure<List<CorperResponseDto>>> getCorpersInUnit(@PathVariable Long unitId) {
        List<CorperResponseDto> corpers = corperService.getAllCorpersInUnit(unitId);
        return ResponseEntity.ok(ApiResponseStructure.success("Corpers in unit retrieved successfully", corpers, HttpStatus.OK.value()));
    }

    @GetMapping("/ppa")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get all Corpers in a PPA (Super Admin only)")
    public ResponseEntity<ApiResponseStructure<List<CorperResponseDto>>> getCorpersInPpa(@AuthenticationPrincipal(expression = "id") Long superAdminId) {
        List<CorperResponseDto> corpers = corperService.getAllCorpersInPpa(superAdminId);
        return ResponseEntity.ok(ApiResponseStructure.success("Corpers in PPA retrieved successfully", corpers, HttpStatus.OK.value()));
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Block a Corper (Super Admin only)")
    public ResponseEntity<ApiResponseStructure<String>> blockCorper(@PathVariable Long id) {
        corperService.blockCorper(id);
        return ResponseEntity.ok(ApiResponseStructure.success("Corper blocked successfully", null, HttpStatus.NO_CONTENT.value()));
    }

    @PutMapping("/{id}/unblock")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Unblock a Corper (Super Admin only)")
    public ResponseEntity<ApiResponseStructure<String>> unblockCorper(@PathVariable Long id) {
        corperService.unblockCorper(id);
        return ResponseEntity.ok(ApiResponseStructure.success("Corper unblocked successfully", null, HttpStatus.NO_CONTENT.value()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Delete a Corper (Admin or Super Admin only)")
    public ResponseEntity<ApiResponseStructure<Void>> deleteCorperByAdmin(@PathVariable Long id) {
        corperService.deleteCorper(id);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Admin deleted successfully", null, HttpStatus.NO_CONTENT.value())
        );
    }
}
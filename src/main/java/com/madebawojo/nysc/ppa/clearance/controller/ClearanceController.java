package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.request.clearance.CancelClearanceRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.clearance.ClearanceRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.clearance.RejectClearanceRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.response.clearance.CancelClearanceResponseDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.clearance.ClearanceResponseDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.clearance.RejectClearanceDto;
import com.madebawojo.nysc.ppa.clearance.service.impl.usercat.SuperAdminServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.clearance.ClearanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/v1/clearances")
@RequiredArgsConstructor
@Tag(name = "Clearance Controllers", description = "Endpoints for clearance request management across Corpers, Unit Heads, and Super Admins")
@SecurityRequirement(name = "bearerAuth")
public class ClearanceController {

    private final ClearanceService clearanceService;
    private final SuperAdminServiceImpl superAdminService;

    // --- CORPER ACTIONS ---
    @PostMapping("/requests")
    @PreAuthorize("hasRole('CORPER')")
    @Operation(
            summary = "Create Clearance Request",
            description = "Allows a corper to create a new clearance request for a given month."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Clearance request created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponseStructure<ClearanceResponseDto>> createRequest(
            @AuthenticationPrincipal(expression = "id") Long corperId,
            @RequestBody @Valid ClearanceRequestDto dto
    ) {
        log.info("Corper {} is creating a clearance request for {}", corperId, dto.getClearanceMonth());
        ClearanceResponseDto response = clearanceService.createRequest(corperId, dto);
        return ResponseEntity.ok(ApiResponseStructure.success("Clearance Request created successfully", response, HttpStatus.CREATED.value()));
    }

    @PatchMapping("/requests/{requestId}")
    @PreAuthorize("hasRole('CORPER')")
    @Operation(
            summary = "Cancel Clearance Request",
            description = "Allows a corper to cancel their pending clearance request with a reason."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clearance request cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid cancellation reason or status not pending"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponseStructure<CancelClearanceResponseDto>> cancelRequest(
            @AuthenticationPrincipal(expression = "id") Long corperId,
            @PathVariable Long requestId
    ) {
        CancelClearanceResponseDto response = clearanceService.cancelRequest(corperId, requestId);
        return ResponseEntity.ok(ApiResponseStructure.success("Clearance Request has been successfully cancelled", response, HttpStatus.OK.value()));
    }

    @GetMapping("/requests")
    @PreAuthorize("hasRole('CORPER')")
    @Operation(
            summary = "Get Corper Requests",
            description = "Fetch all clearance requests created by the currently authenticated corper."
    )
    @ApiResponse(responseCode = "200", description = "List of clearance requests retrieved successfully")
    public ResponseEntity<ApiResponseStructure<List<ClearanceResponseDto>>> getRequestsForCorper(@AuthenticationPrincipal(expression = "id") Long corperId) {
        List<ClearanceResponseDto> response = clearanceService.getRequestsForCorper(corperId);
        return ResponseEntity.ok(ApiResponseStructure.success(
                "All clearance requests for corper retrieved successfully", response, HttpStatus.OK.value())
        );
    }

    @DeleteMapping("/my-requests")
    @PreAuthorize("hasRole('CORPER')")
    @Operation(
            summary = "Delete all my clearance requests",
            description = "Allows an authenticated corper to delete all the clearance requests they have previously made."
    )
    public ResponseEntity<ApiResponseStructure<String>> deleteMyClearanceRequests(
            @AuthenticationPrincipal(expression = "id") Long corperId
    ) {

        // Safety: corperId should always come from the authenticated user
        clearanceService.deleteAllMyClearanceRequests(corperId);
        return ResponseEntity.ok(
                ApiResponseStructure.success(
                        "All your clearance requests have been successfully deleted.",
                        null,
                        HttpStatus.OK.value()
                )
        );
    }

    // --- UNIT HEAD ACTIONS ---
    @PatchMapping("/unit-head/approve/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Approve Clearance Request (Unit Head)",
            description = "Allows a unit head to approve a clearance request within their unit."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request approved successfully"),
            @ApiResponse(responseCode = "404", description = "Clearance request not found"),
    })
    public ResponseEntity<ApiResponseStructure<ClearanceResponseDto>> approveByUnitHead(
            @AuthenticationPrincipal(expression = "id") Long unitHeadId,
            @PathVariable Long requestId
    ) {
        ClearanceResponseDto response = clearanceService.approveByUnitHead(requestId, unitHeadId);
        return ResponseEntity.ok(ApiResponseStructure.success("Approved successfully", response, HttpStatus.OK.value()));
    }

    @PatchMapping("/unit-head/reject/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Reject Clearance Request (Unit Head)",
            description = "Allows a unit head to reject a clearance request with a reason."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request rejected successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid reason"),
    })
//    public ResponseEntity<ApiResponseStructure<RejectClearanceDto>> rejectByUnitHead(
//            @AuthenticationPrincipal(expression = "id") Long unitHeadId,
//            @Valid @PathVariable Long requestId
//    ) {
//        RejectClearanceDto response = clearanceService.rejectByUnitHead(requestId, unitHeadId);
//        return ResponseEntity.ok(ApiResponseStructure.success("Clearance request rejected successfully", response, HttpStatus.OK.value()));
//
//    }

    public ResponseEntity<ApiResponseStructure<RejectClearanceDto>> rejectByUnitHead(
            @AuthenticationPrincipal(expression = "id") Long unitHeadId,
            @Valid @PathVariable Long requestId, @Valid @RequestBody RejectClearanceRequestDto rejectRequest
    ) {
        RejectClearanceDto response = clearanceService.rejectByUnitHead(requestId, unitHeadId, rejectRequest.getReason());
        return ResponseEntity.ok(ApiResponseStructure.success("Clearance request rejected successfully", response, HttpStatus.OK.value()));

    }

    @GetMapping("/unit-head/requests")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get Clearance Requests for Unit",
            description = "Fetch all clearance requests submitted within a unit."
    )
    @ApiResponse(responseCode = "200", description = "List of clearance requests retrieved successfully")
    public ResponseEntity<ApiResponseStructure<List<ClearanceResponseDto>>> getRequestsForUnit(@AuthenticationPrincipal(expression = "id") Long unitHeadId) {
        List<ClearanceResponseDto> response = clearanceService.getRequestsForUnit(unitHeadId);
        return ResponseEntity.ok(ApiResponseStructure.success(
                "All clearance requests for unit retrieved successfully", response, HttpStatus.OK.value())
        );

    }


    // --- SUPER ADMIN ACTIONS ---
    @PatchMapping("/super-admin/approve/{requestId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Approve Clearance Request (SuperAdmin)",
            description = "Allows a super admin to approve a clearance request within their unit."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request approved successfully"),
            @ApiResponse(responseCode = "404", description = "Clearance request not found"),
    })
    public ResponseEntity<ApiResponseStructure<ClearanceResponseDto>> approveBySuperAdmin(
            @AuthenticationPrincipal(expression = "id") Long superAdminId,
            @PathVariable Long requestId
    ) {
        ClearanceResponseDto response = clearanceService.approveBySuperAdmin(requestId, superAdminId);
        return ResponseEntity.ok(ApiResponseStructure.success("Clearance request approved successfully", response, HttpStatus.OK.value()));
    }

    @PatchMapping("/super-admin/reject/{requestId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Reject Clearance Request (Super Admin)",
            description = "Allows a super admin to reject a clearance request with a reason."
    )
    @ApiResponse(responseCode = "200", description = "Clearance request rejected successfully")
//    public ResponseEntity<ApiResponseStructure<RejectClearanceDto>> rejectBySuperAdmin(
//            @AuthenticationPrincipal(expression = "id") Long superAdminId,
//            @Valid @PathVariable Long requestId
//    ) {
//        RejectClearanceDto response = clearanceService.rejectBySuperAdmin(requestId, superAdminId);
//        return ResponseEntity.ok(ApiResponseStructure.success("Clearance requests retrieved successfully", response, HttpStatus.OK.value()));
//    }

    public ResponseEntity<ApiResponseStructure<RejectClearanceDto>> rejectBySuperAdmin(
            @AuthenticationPrincipal(expression = "id") Long superAdminId,
            @Valid @PathVariable Long requestId,  @Valid @RequestBody RejectClearanceRequestDto rejectRequest
    ) {
        RejectClearanceDto response = clearanceService.rejectBySuperAdmin(requestId, superAdminId, rejectRequest.getReason());
        return ResponseEntity.ok(ApiResponseStructure.success("Clearance requests retrieved successfully", response, HttpStatus.OK.value()));
    }

    @GetMapping("/ppa/requests")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Get Clearance Requests for PPA",
            description = "Fetch all clearance requests associated with the super admin’s PPA."
    )
    @ApiResponse(responseCode = "200", description = "Clearance requests retrieved successfully")
    public ResponseEntity<ApiResponseStructure<List<ClearanceResponseDto>>> getRequestsForPpa(
            @AuthenticationPrincipal(expression = "id") Long superAdminId) {

        Long ppaId = superAdminService.getSuperAdminEntityById(superAdminId).getPpa().getId();

        List<ClearanceResponseDto> response = clearanceService.getAllRequestsForPpa(ppaId);

        return ResponseEntity.ok(ApiResponseStructure.success("Clearance requests for PPA retrieved successfully", response, HttpStatus.OK.value()));
    }
}


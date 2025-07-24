package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.request.CorperRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.UpdateCredentialsRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.CorperResponseDto;
import com.madebawojo.nysc.ppa.clearance.service.impl.CorperServiceImpl;
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
public class CorperController {

    private final CorperServiceImpl corperService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponseStructure<CorperResponseDto>> createCorper(@Valid @RequestBody CorperRequestDto dto) {
        CorperResponseDto response = corperService.createCorper(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.getId()).toUri();

        log.info("Location header for created corper: {}", location);

        return ResponseEntity
                .created(location)
                .body(ApiResponseStructure.success("Corper created successfully", response, HttpStatus.CREATED.value()));
    }

    // Get Corper Profile (Authenticated Corper)
    @GetMapping("/me")
    public ResponseEntity<ApiResponseStructure<CorperResponseDto>> retrieveAuthenticatedCorper(@AuthenticationPrincipal(expression = "id") Long userId) {
        CorperResponseDto response = corperService.getCorperById(userId);
        return ResponseEntity.ok(ApiResponseStructure.success("Corper retrieved successfully", response, HttpStatus.OK.value()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<CorperResponseDto>> retrieveCorperWithIdByAdmin(Long userId) {
        CorperResponseDto response = corperService.getCorperById(userId);
        return ResponseEntity.ok(ApiResponseStructure.success("Corper retrieved successfully", response, HttpStatus.OK.value()));
    }

    // Update Corper Profile
    @PutMapping("/me")
    public ResponseEntity<ApiResponseStructure<CorperResponseDto>> updateCorper(@AuthenticationPrincipal(expression = "id") Long userId,
                                                                                @Valid @RequestBody CorperRequestDto dto) {
        CorperResponseDto response = corperService.updateCorper(userId, dto);
        return ResponseEntity.ok(ApiResponseStructure.success("Corper updated successfully", response, HttpStatus.OK.value()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{id}")
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

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/unit/{unitId}")
    public ResponseEntity<ApiResponseStructure<List<CorperResponseDto>>> getCorpersInUnit(@PathVariable Long unitId) {
        List<CorperResponseDto> corpers = corperService.getAllCorpersInUnit(unitId);
        return ResponseEntity.ok(ApiResponseStructure.success("Corpers in unit retrieved successfully", corpers, HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/ppa/{ppaId}")
    public ResponseEntity<ApiResponseStructure<List<CorperResponseDto>>> getCorpersInPpa(@PathVariable Long ppaId) {
        List<CorperResponseDto> corpers = corperService.getAllCorpersInUnit(ppaId);
        return ResponseEntity.ok(ApiResponseStructure.success("Corpers in unit retrieved successfully", corpers, HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/block")
    public ResponseEntity<ApiResponseStructure<String>> blockCorper(@PathVariable Long id) {
        corperService.blockCorper(id);
        return ResponseEntity.ok(ApiResponseStructure.success("Corper blocked successfully", null, HttpStatus.NO_CONTENT.value()));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/unblock")
    public ResponseEntity<ApiResponseStructure<String>> unblockCorper(@PathVariable Long id) {
        corperService.unblockCorper(id);
        return ResponseEntity.ok(ApiResponseStructure.success("Corper unblocked successfully", null, HttpStatus.NO_CONTENT.value()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<Void>> deleteAdmin(@PathVariable Long id) {
        corperService.deleteCorper(id);
        return ResponseEntity.ok(
                ApiResponseStructure.success("Admin deleted successfully", null, HttpStatus.NO_CONTENT.value())
        );
    }
}





//package com.madebawojo.nysc.ppa.clearance.controller;
//
//import com.madebawojo.nysc.ppa.clearance.core.response.ApiResponseStructure;
//import com.madebawojo.nysc.ppa.clearance.dto.request.CorperRequestDto;
//import com.madebawojo.nysc.ppa.clearance.dto.request.UpdateCredentialsRequest;
//import com.madebawojo.nysc.ppa.clearance.dto.response.CorperResponseDto;
//import com.madebawojo.nysc.ppa.clearance.service.impl.CorperServiceImpl;
//import com.madebawojo.nysc.ppa.clearance.service.servicecontract.CorperService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/corpers")
//@RequiredArgsConstructor
//public class CorperController {
//
//    private final CorperServiceImpl corperService;
//
//    // ✅ Create Corper (Admin Operation)
//    @PostMapping
//    public ResponseEntity<ApiResponseStructure<CorperResponseDto>> createCorper(@Valid @RequestBody CorperRequestDto dto) {
//        CorperResponseDto response = corperService.createCorper(dto);
//        return ResponseEntity.ok(ApiResponseStructure.success("Corper created successfully", response, HttpStatus.OK.value()));
//    }
//
//    // Get Corper by Email (For Authenticated Corper)
//    @GetMapping("/me")
//    public ResponseEntity<ApiResponseStructure<CorperResponseDto>> getCorper(@RequestParam Long userId) {
//        CorperResponseDto response = corperService.getCorperById(userId);
//        return ResponseEntity.ok(ApiResponseStructure.success("Corper retrieved successfully", response, HttpStatus.OK.value()));
//    }
//
//    // ✅ Update Corper Profile (For Authenticated Corper)
//    @PutMapping("/me")
//    public ResponseEntity<ApiResponseStructure<CorperResponseDto>> updateCorper(@RequestParam Long userId, @Valid @RequestBody CorperRequestDto dto) {
//        CorperResponseDto response = corperService.updateCorper(userId, dto);
//        return ResponseEntity.ok(ApiResponseStructure.success("Corper updated successfully", response, HttpStatus.OK.value()));
//    }
//
//    // ✅ Update Email/Password (For Authenticated Corper)
//    @PutMapping("/me/credentials")
//    public ResponseEntity<ApiResponseStructure<String>> updateCredentials(@RequestParam String email, @Valid @RequestBody UpdateCredentialsRequest request) {
//        corperService.updateCredentials(email, request);
//        return ResponseEntity.ok(ApiResponseStructure.success("Credentials updated successfully", null, HttpStatus.OK.value()));
//    }
//
//    // ✅ Get All Corpers in a Unit (Admin Operation)
//    @GetMapping("/unit/{unitId}")
//    public ResponseEntity<ApiResponseStructure<List<CorperResponseDto>>> getCorpersInUnit(@PathVariable Long unitId) {
//        List<CorperResponseDto> corpers = corperService.getAllCorpersInUnit(unitId);
//        return ResponseEntity.ok(ApiResponseStructure.success("Corpers in unit retrieved successfully", corpers, HttpStatus.OK.value()));
//    }
//
//    // ✅ Block Corper (Admin Operation)
//    @PutMapping("/{id}/block")
//    public ResponseEntity<ApiResponseStructure<String>> blockCorper(@PathVariable Long id) {
//        corperService.blockCorper(id);
//        return ResponseEntity.ok(ApiResponseStructure.success("Corper blocked successfully", null, HttpStatus.OK.value()));
//    }
//
//    // ✅ Unblock Corper (Admin Operation)
//    @PutMapping("/{id}/unblock")
//    public ResponseEntity<ApiResponseStructure<String>> unblockCorper(@PathVariable Long id) {
//        corperService.unblockCorper(id);
//        return ResponseEntity.ok(ApiResponseStructure.success("Corper unblocked successfully", null, HttpStatus.OK.value()));
//    }
//}

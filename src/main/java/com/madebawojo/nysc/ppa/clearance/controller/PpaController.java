package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.request.ppa.UpdatePpaRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.request.ppa.PpaRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.ppa.PpaResponseDto;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.ppa.PpaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/ppas")
@RequiredArgsConstructor
@Tag(name = "PPAs", description = "Manage Places of Primary Assignment (PPA). Restricted to Global Admins.")
@SecurityRequirement(name = "bearerAuth")
public class PpaController {

    private final PpaService ppaService;

    @PostMapping
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @Operation(
            summary = "Create a new PPA",
            description = "Accessible only by GLOBAL_ADMIN. Creates a new Place of Primary Assignment (PPA).",
            responses = {
                    @ApiResponse(responseCode = "201", description = "PPA created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - requires GLOBAL_ADMIN role")
            }
    )
    public ResponseEntity<ApiResponseStructure<PpaResponseDto>> createPpa(@RequestBody PpaRequestDto dto) {
        log.info("######################GLOBAL ADMIN ACTION!!!###########################");
        log.info("Creating PPA...");

        PpaResponseDto createdPpa = ppaService.createPpa(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdPpa.getId()).toUri();

        log.info("Location header for created PPA: {}", location);
        return ResponseEntity
                .created(location)
                .body(ApiResponseStructure.success("PPA created successfully", createdPpa, HttpStatus.CREATED.value()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @Operation(
            summary = "Get PPA by ID",
            description = "Fetch a specific PPA by its ID. Accessible only by GLOBAL_ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "PPA retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "PPA not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - requires GLOBAL_ADMIN role")
            }
    )
    public ResponseEntity<ApiResponseStructure<PpaResponseDto>> getPpaById(@PathVariable Long id) {
        log.info("######################GLOBAL ADMIN ACTION!!!###########################");
        log.info("Retrieving PPA with ID: {}", id);

        PpaResponseDto ppa = ppaService.getPpaById(id);
        return ResponseEntity.ok(ApiResponseStructure.success("PPA retrieved", ppa, HttpStatus.OK.value()));
    }

    @GetMapping
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @Operation(
            summary = "Get all PPAs",
            description = "Fetch all Places of Primary Assignment (PPA). Accessible only by GLOBAL_ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of all PPAs retrieved successfully"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - requires GLOBAL_ADMIN role")
            }
    )
    public ResponseEntity<ApiResponseStructure<List<PpaResponseDto>>> getAllPpas() {
        log.info("######################GLOBAL ADMIN ACTION!!!###########################");
        log.info("Retrieving all PPAs...");

        List<PpaResponseDto> list = ppaService.getAllPpas();
        return ResponseEntity.ok(ApiResponseStructure.success("All PPAs retrieved", list, HttpStatus.OK.value()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @Operation(
            summary = "Update a PPA",
            description = "Update the details of a specific PPA by ID. Accessible only by GLOBAL_ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "PPA updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "PPA not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - requires GLOBAL_ADMIN role")
            }
    )
    public ResponseEntity<ApiResponseStructure<PpaResponseDto>> updatePpa(@PathVariable Long id,
                                                                          @RequestBody UpdatePpaRequestDto dto) {
        log.info("######################GLOBAL ADMIN ACTION!!!###########################");
        log.info("Updating PPA with ID: {}", id);

        PpaResponseDto updated = ppaService.updatePpa(id, dto);
        return ResponseEntity.ok(ApiResponseStructure.success("PPA updated successfully", updated, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @Operation(
            summary = "Delete a PPA",
            description = "Delete a specific PPA by ID. Accessible only by GLOBAL_ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "PPA deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "PPA not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - requires GLOBAL_ADMIN role")
            }
    )
    public ResponseEntity<ApiResponseStructure<?>> deletePpa(@PathVariable Long id) {
        log.info("######################GLOBAL ADMIN ACTION!!!###########################");
        log.warn("Sending request to delete PPA, with ID: {}", id);

        ppaService.deletePpa(id);
        return ResponseEntity.ok(ApiResponseStructure.success("PPA deleted successfully", null, HttpStatus.OK.value()));
    }
}

//TODO: Create get all corpers in ppa
//TODO: Incase any validation fails in user creation, saved data should be rolled back. Check logic and adjust for efficiency. Context: 2025-07-18T11:33:27.413+01:00  WARN 62104 --- [nio-8080-exec-5] .m.m.a.ExceptionHandlerExceptionResolver : Resolved [org.springframework.web.bind.MethodArgumentNotValidException: Validation failed for argument [0] in public org.springframework.http.ResponseEntity<com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure<com.madebawojo.nysc.ppa.clearance.dto.response.usercat.SuperAdminResponseDto>> com.madebawojo.nysc.ppa.clearance.controller.SuperAdminController.createSuperAdmin(com.madebawojo.nysc.ppa.clearance.dto.request.usercat.SuperAdminRequestDto): [Field error in object 'superAdminRequestDto' on field 'password': rejected value [testP]; codes [Size.superAdminRequestDto.password,Size.password,Size.java.lang.String,Size]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [superAdminRequestDto.password,password]; arguments []; default message [password],20,8]; default message [Password must be between 8 and 20 characters]] ]
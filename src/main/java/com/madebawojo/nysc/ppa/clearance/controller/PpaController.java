package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.request.UpdatePpaRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import com.madebawojo.nysc.ppa.clearance.dto.request.PpaRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.PpaResponseDto;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.PpaService;
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
public class PpaController {

    private final PpaService ppaService;

    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @PostMapping
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

    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<PpaResponseDto>> getPpaById(@PathVariable Long id) {
        log.info("######################GLOBAL ADMIN ACTION!!!###########################");
        log.info("Retrieving PPA with ID: {}", id);

        PpaResponseDto ppa = ppaService.getPpaById(id);
        return ResponseEntity.ok(ApiResponseStructure.success("PPA retrieved", ppa, HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponseStructure<List<PpaResponseDto>>> getAllPpas() {
        log.info("######################GLOBAL ADMIN ACTION!!!###########################");
        log.info("Retrieving all PPAs...");

        List<PpaResponseDto> list = ppaService.getAllPpas();
        return ResponseEntity.ok(ApiResponseStructure.success("All PPAs retrieved", list, HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<PpaResponseDto>> updatePpa(@PathVariable Long id,
                                                                          @RequestBody UpdatePpaRequestDto dto) {
        log.info("######################GLOBAL ADMIN ACTION!!!###########################");
        log.info("Updating PPA with ID: {}", id);

        PpaResponseDto updated = ppaService.updatePpa(id, dto);
        return ResponseEntity.ok(ApiResponseStructure.success("PPA updated successfully", updated, HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseStructure<?>> deletePpa(@PathVariable Long id) {
        log.info("######################GLOBAL ADMIN ACTION!!!###########################");
        log.warn("Sending request to delete PPA, with ID: {}", id);

        ppaService.deletePpa(id);
        return ResponseEntity.ok(ApiResponseStructure.success("PPA deleted successfully", null, HttpStatus.OK.value()));
    }
}

//TODO: Create get all corpers in ppa
//TODO: Incase any validation fails in user creation, saved data should be rolled back. Check logic and adjust for efficiency. Context: 2025-07-18T11:33:27.413+01:00  WARN 62104 --- [nio-8080-exec-5] .m.m.a.ExceptionHandlerExceptionResolver : Resolved [org.springframework.web.bind.MethodArgumentNotValidException: Validation failed for argument [0] in public org.springframework.http.ResponseEntity<com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure<com.madebawojo.nysc.ppa.clearance.dto.response.SuperAdminResponseDto>> com.madebawojo.nysc.ppa.clearance.controller.SuperAdminController.createSuperAdmin(com.madebawojo.nysc.ppa.clearance.dto.request.SuperAdminRequestDto): [Field error in object 'superAdminRequestDto' on field 'password': rejected value [testP]; codes [Size.superAdminRequestDto.password,Size.password,Size.java.lang.String,Size]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [superAdminRequestDto.password,password]; arguments []; default message [password],20,8]; default message [Password must be between 8 and 20 characters]] ]
package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.dto.response.ApiResponseStructure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Month;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {
    @GetMapping("/clearance-months")
    public ResponseEntity<ApiResponseStructure<List<String>>> getMonths() {
        List<String> months = Arrays.stream(Month.values())
                .map(Enum::name)
                .toList();

        return ResponseEntity.ok(ApiResponseStructure.success("Available months retrieved successfully", months, HttpStatus.OK.value()));
    }
}

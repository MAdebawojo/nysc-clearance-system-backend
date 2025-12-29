package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.service.impl.clearance.ClearancePdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/clearance-docs")
@Tag(name = "Clearance Documents", description = "Generate, view, download, and print clearance documents.")
@SecurityRequirement(name = "bearerAuth")
public class ClearanceDocumentController {

    private final ClearancePdfService clearancePdfService;

    @Operation(
            summary = "Download clearance PDF",
            description = "Downloads the clearance letter as a PDF file for a given clearance request ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "PDF file downloaded successfully",
                            content = @Content(mediaType = "application/pdf")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Clearance request not cleared or invalid request ID",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - only SUPER_ADMIN can access this endpoint",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error during PDF generation",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    @GetMapping("/{requestId}/download")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<byte[]> downloadClearancePdf(@PathVariable Long requestId) {
        byte[] pdf = clearancePdfService.generateClearancePdf(requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "clearance.pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @Operation(
            summary = "View clearance PDF",
            description = "Streams the clearance letter as a PDF (inline view) for a given clearance request ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "PDF file streamed successfully",
                            content = @Content(mediaType = "application/pdf")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Clearance request not cleared or invalid request ID",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - only SUPER_ADMIN can access this endpoint",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error during PDF generation",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    @GetMapping("/{requestId}/view")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<byte[]> viewClearancePdf(@PathVariable Long requestId) {
        byte[] pdf = clearancePdfService.generateClearancePdf(requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("clearance.pdf").build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

}
package com.madebawojo.nysc.ppa.clearance.controller;

import com.madebawojo.nysc.ppa.clearance.service.impl.clearance.ClearancePdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
public class ClearanceDocumentController {

    //    private final PdfGeneratorService pdfGeneratorService;
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






















//    @GetMapping("/view")
//    public String index(Model model) {
//        String clearanceLetterDate = DateFormatUtil.formatDateToReadableString(LocalDate.now());
//        String corpsMemberName = "Sodolamu Emmanuel";
//        String stateCode = "LA/25A/78920";
//        String callUpNumber = "NYSC/BCU/123456";
//        String clearanceMonthUpper = "SEPTEMBER".toUpperCase();
//        String signatoryName = "Abidun Ojo";
//        String signatoryTitle = "Head Human Resources";
//
//        model.addAttribute("clearanceLetterDate", clearanceLetterDate);
//        model.addAttribute("corpsMemberName", corpsMemberName);
//        model.addAttribute("stateCode", stateCode);
//        model.addAttribute("callUpNumber", callUpNumber);
//        model.addAttribute("clearanceMonthUpper", clearanceMonthUpper);
//        model.addAttribute("signatoryName", signatoryName);
//        model.addAttribute("signatoryTitle", signatoryTitle);
//
//        return "clearance/clearance-template.html";
//    }

//    @GetMapping("/download-pdf")
//    public ResponseEntity<ApiResponseStructure<byte[]>> generatePdf() throws Exception {
//        Context context = new Context();
//        context.setVariable("clearanceLetterDate", DateFormatUtil.formatDateToReadableString(LocalDate.now()));
//        context.setVariable("corpsMemberName", "Sodolamu Emmanuel");
//        context.setVariable("stateCode", "LA/25A/78920");
//        context.setVariable("callUpNumber", "NYSC/BCU/123456");
//        context.setVariable("clearanceMonthUpper", "SEPTEMBER".toUpperCase());
//        context.setVariable("signatoryName", "Abidun Ojo");
//        context.setVariable("signatoryTitle", "Head Human Resources");
//
//        byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml("clearance/clearance-template", context);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDispositionFormData("attachment", "clearance.pdf");
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(ApiResponseStructure.success("Clearance PDF downloaded successfully", pdfBytes, HttpStatus.OK.value()));
//    }
//@GetMapping("/download/{corperId}")
//public ResponseEntity<ApiResponseStructure<byte[]>> generatePdf(@PathVariable int corperId) {
//    log.info("Starting clearance PDF generation...");
//
//    try {
//        // Prepare context
//        Context context = new Context();
//        context.setVariable("clearanceLetterDate", DateFormatUtil.formatDateToReadableString(LocalDate.now()));
//        context.setVariable("corpsMemberName", "Sodolamu Emmanuel");
//        context.setVariable("stateCode", "LA/25A/78920");
//        context.setVariable("callUpNumber", "NYSC/BCU/123456");
//        context.setVariable("clearanceMonthUpper", "SEPTEMBER".toUpperCase());
//        context.setVariable("signatoryName", "Abidun Ojo");
//        context.setVariable("signatoryTitle", "Head Human Resources");
//
//        log.debug("Context prepared with corps member details: {}, stateCode: {}, callUpNumber: {}",
//                "Sodolamu Emmanuel", "LA/25A/78920", "NYSC/BCU/123456");
//
//        // Generate PDF
//        byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml("clearance/clearance-template", context);
//
//        log.info("Clearance PDF generated successfully, size: {} bytes", pdfBytes.length);
//
//        // Build response
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDispositionFormData("attachment","clearance.pdf");
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(ApiResponseStructure.success(
//                        "Clearance PDF downloaded successfully",
//                        pdfBytes,
//                        HttpStatus.OK.value()
//                ));
//
//    } catch (IOException e) {
//        log.error("I/O error while generating clearance PDF", e);
//        throw new ApiException("Error writing PDF response: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//
//    } catch (Exception e) {
//        log.error("Unexpected error during clearance PDF generation", e);
//        throw new ApiException("Error generating clearance PDF: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}




















//import com.madebawojo.nysc.ppa.clearance.core.exception.ApiException;
//import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Corper;
//import com.madebawojo.nysc.ppa.clearance.service.impl.usercat.CorperServiceImpl;
//import com.madebawojo.nysc.ppa.clearance.util.DateFormatUtil;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.context.Context;
//import org.xhtmlrenderer.pdf.ITextRenderer;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.time.LocalDate;
//
//@Slf4j
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/clearance-docs")
//public class ClearanceGenerator {
//    private final CorperServiceImpl corperService;
//    private final TemplateEngine templateEngine;
//
//
//    @GetMapping("/view")
//    public String index(Model model) {
//        String clearanceLetterDate = DateFormatUtil.formatDateToReadableString(LocalDate.now());
//        String corpsMemberName = "Sodolamu Emmanuel";
//        String stateCode = "LA/25A/78920";
//        String callUpNumber = "NYSC/BCU/123456";
//        String clearanceMonthUpper = "SEPTEMBER".toUpperCase();
//        String signatoryName = "Abidun Ojo";
//        String signatoryTitle = "Head Human Resources";
//
//        model.addAttribute("clearanceLetterDate", clearanceLetterDate);
//        model.addAttribute("corpsMemberName", corpsMemberName);
//        model.addAttribute("stateCode", stateCode);
//        model.addAttribute("callUpNumber", callUpNumber);
//        model.addAttribute("clearanceMonthUpper", clearanceMonthUpper);
//        model.addAttribute("signatoryName", signatoryName);
//        model.addAttribute("signatoryTitle", signatoryTitle);
//
//        return "clearance/clearance-template.html";
//    }
//
//    @GetMapping("/download/{corperId}")
//    public void downloadClearance(@PathVariable int corperId, HttpServletResponse response) {
//        try {
//            // prepare Thymeleaf context
//            Context context = new Context();
////            context.setVariable("clearanceLetterDate", DateFormatUtil.formatDateToReadableString(LocalDate.now()));
////            context.setVariable("corpsMemberName", "Sodolamu Emmanuel");
////            context.setVariable("stateCode", "LA/25A/78920");
////            context.setVariable("callUpNumber", "NYSC/BCU/123456");
////            context.setVariable("clearanceMonthUpper", "SEPTEMBER".toUpperCase());
////            context.setVariable("signatoryName", "Abidun Ojo");
////            context.setVariable("signatoryTitle", "Head Human Resources");
//              context.setVariable("name", "Adebawojo");
//            // render HTML with Thymeleaf
////            String html = templateEngine.process("clearance/clearance-template", context);
//            String html = templateEngine.process("clearance/hello", context);
//
//            log.info("Generated HTML length = {}", html.length());
//
//            // set response headers
//            response.setContentType("application/pdf");
//            response.setHeader("Content-Disposition", "attachment; filename=clearance.pdf");
//
//            try (OutputStream os = response.getOutputStream()) {
//                ITextRenderer renderer = new ITextRenderer();
//
//                // set base URL for resources (CSS, images, etc.)
////                String baseUrl = new ClassPathResource("templates/clearance/").getURL().toString();
//                renderer.setDocumentFromString(html);
//
//                renderer.layout();
//                renderer.createPDF(os, false);
//                renderer.finishPDF();
//            }
//
//            log.info("Successfully generated clearance PDF for corper ID: {}", corperId);
//
//        } catch (IOException e) {
//            log.error("I/O error while generating clearance PDF for corper ID: {}", corperId, e);
//            throw new ApiException("Error writing PDF response: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//
//        } catch (Exception e) {
//            log.error("Unexpected error while generating clearance PDF for corper ID: {}", corperId, e);
//            throw new ApiException("Error generating clearance PDF: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
////    @GetMapping("/download/{corperId}")
////    public void downloadClearance(@PathVariable int corperId, HttpServletResponse response) {
////        try {
////            // fetch corper details from DB (only cleared corpers)
//////            Corper corper = corperService.getClearedCorper(corperId);
////
////            // prepare Thymeleaf context
////            Context context = new Context();
////
////            context.setVariable("clearanceLetterDate", DateFormatUtil.formatDateToReadableString(LocalDate.now()));
////            context.setVariable("corpsMemberName", "Sodolamu Emmanuel");
////            context.setVariable("stateCode", "LA/25A/78920");
////            context.setVariable("callUpNumber", "NYSC/BCU/123456");
////            context.setVariable("clearanceMonthUpper", "SEPTEMBER".toUpperCase());
////            context.setVariable("signatoryName", "Abidun Ojo");
////            context.setVariable("signatoryTitle", "Head Human Resources");
////
////
//////            context.setVariable("clearanceLetterDate", DateFormatUtil.formatDateToReadableString(LocalDate.now()));
//////            context.setVariable("corpsMemberName", corper.getUser().getFullName());
//////            context.setVariable("stateCode", corper.getStateCode());
//////            context.setVariable("callUpNumber", corper.getCallUpNumber());
//////            context.setVariable("clearanceMonthUpper", LocalDate.now().getMonth().toString());
//////            context.setVariable("signatoryName", "Abidun Ojo");
//////            context.setVariable("signatoryTitle", "Head Human Resources");
////
////
////            String html = templateEngine.process("clearance/clearance-template", context);
////
////            // set response headers
////            response.setContentType("application/pdf");
////            response.setHeader("Content-Disposition", "attachment; filename=clearance.pdf");
////
////            try (OutputStream os = response.getOutputStream()) {
////                ITextRenderer renderer = new ITextRenderer();
////                renderer.setDocumentFromString(html);
////                renderer.layout();
////                renderer.createPDF(os);
////            }
////
////            log.info("Successfully generated clearance PDF for corper ID: {}", corperId);
////
////        } catch (IOException e) {
////            log.error("I/O error while generating clearance PDF for corper ID: {}", corperId, e);
////            throw new ApiException("Error writing PDF response " + e, HttpStatus.INTERNAL_SERVER_ERROR);
////
////        } catch (Exception e) {
////            log.error("Unexpected error while generating clearance PDF for corper ID: {}", corperId, e);
////            throw new ApiException("Error generating clearance PDF" + e, HttpStatus.INTERNAL_SERVER_ERROR);
////        }
////    }
//
//
//}

package com.madebawojo.nysc.ppa.clearance.service.impl.clearance;

import com.madebawojo.nysc.ppa.clearance.core.enums.ClearanceStatus;
import com.madebawojo.nysc.ppa.clearance.core.exception.ApiException;
import com.madebawojo.nysc.ppa.clearance.dto.mapper.ClearanceLetterMapper;
import com.madebawojo.nysc.ppa.clearance.entity.clearance.ClearanceRequest;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.repository.UserRepository;
import com.madebawojo.nysc.ppa.clearance.util.ClearanceLetterVariables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClearancePdfService {

    private final UserRepository userRepository;
    private final ClearanceServiceImpl clearanceRequestService;
    private final ClearanceTemplateContextBuilder contextBuilder;
    private final PdfGeneratorService pdfGeneratorService;

    public byte[] generateClearancePdf(Long requestId) {
        ClearanceRequest request = clearanceRequestService.getClearanceRequest(requestId);
        if (request.getStatus() != ClearanceStatus.CLEARED){
            log.warn("Clearance request [{}] is not cleared. Status: {}", requestId, request.getStatus());
            throw new ApiException("Failed to generate clearance PDF.", HttpStatus.BAD_REQUEST);
        }

        Long corperId = request.getCorper().getId();
        User user = userRepository.findById(corperId)
                .orElseThrow(() -> {
                    log.warn("User not found for corperId: {}", corperId);
                    return new ApiException("User does not exist", HttpStatus.BAD_REQUEST);
                });

        ClearanceLetterVariables variables = ClearanceLetterMapper.toVariables(request, user);
        Context context = contextBuilder.build(variables);

        try {
            log.info("Generating clearance PDF for corps member: {}", variables.getCorpsMemberName());
            byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml("clearance/clearance-template", context);
            log.info("Clearance PDF generated successfully, size: {} bytes", pdfBytes.length);
            return pdfBytes;
        } catch (IOException e) {
            log.error("I/O error while generating clearance PDF", e);
            throw new ApiException("Error writing PDF response", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error during clearance PDF generation", e);
            throw new ApiException("Error generating clearance PDF", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


package com.madebawojo.nysc.ppa.clearance.service.impl.clearance;

import com.madebawojo.nysc.ppa.clearance.core.enums.ClearanceStatus;
import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
import com.madebawojo.nysc.ppa.clearance.core.exception.ApiException;
import com.madebawojo.nysc.ppa.clearance.core.exception.ResourceNotFoundException;
import com.madebawojo.nysc.ppa.clearance.core.exception.UnauthorizedException;
import com.madebawojo.nysc.ppa.clearance.dto.mapper.ClearanceMapper;
import com.madebawojo.nysc.ppa.clearance.dto.request.clearance.ClearanceRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.clearance.CancelClearanceResponseDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.clearance.ClearanceResponseDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.clearance.RejectClearanceDto;
import com.madebawojo.nysc.ppa.clearance.entity.clearance.ClearanceRequest;
import com.madebawojo.nysc.ppa.clearance.entity.ppa.Ppa;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Admin;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Corper;
import com.madebawojo.nysc.ppa.clearance.repository.ClearanceRequestRepository;
import com.madebawojo.nysc.ppa.clearance.repository.UserRepository;
import com.madebawojo.nysc.ppa.clearance.service.impl.ppa.PpaServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.impl.ppa.UnitServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.impl.usercat.AdminServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.impl.usercat.CorperServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.clearance.ClearanceService;
import com.madebawojo.nysc.ppa.clearance.util.ClearanceLetterVariables;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ClearanceServiceImpl implements ClearanceService {

    private final UserRepository userRepository;
    private final ClearanceRequestRepository clearanceRepo;
//    private final CorperRepository corperRepo;
//    private final AdminRepository adminRepo;

    private final UnitServiceImpl unitService;
    private final PpaServiceImpl ppaService;
    private final CorperServiceImpl corperService;
    private final AdminServiceImpl adminService;
    private final PdfGeneratorService pdfGeneratorService;

    @Override
    public ClearanceResponseDto createRequest(Long corperId, ClearanceRequestDto dto) {
        Corper corper = corperService.getCorperEntityById(corperId);

        validateMonthAllowed(dto.getClearanceMonth());

        if (dto.getTentativeDate().isBefore(LocalDate.now())) {
            throw new ApiException("Tentative day cannot be in the past", HttpStatus.BAD_REQUEST);
        }

//        Optional<ClearanceRequest> existingRequest = clearanceRepo.findByCorper_IdAndClearanceMonth(corperId, dto.getClearanceMonth());
        List<ClearanceRequest> requests = clearanceRepo.findByCorper_IdAndClearanceMonth(
                corperId, dto.getClearanceMonth()
        );

        Optional<ClearanceRequest> existingRequest = requests.stream()
                .max(Comparator.comparing(ClearanceRequest::getCreatedAt));

        existingRequest.ifPresent(req -> {
            if (req.getStatus() != ClearanceStatus.REJECTED && req.getStatus() != ClearanceStatus.CANCELLED) {
                throw new ApiException("You already requested clearance for that month", HttpStatus.CONFLICT);
            }
        });

        ClearanceRequest req = ClearanceRequest.builder()
                .corper(corper)
                .tentativeDate(dto.getTentativeDate())
                .clearanceMonth(dto.getClearanceMonth())
                .build();

        clearanceRepo.save(req);

        return ClearanceMapper.toDto(req);
    }

    @Override
    public CancelClearanceResponseDto cancelRequest(Long corperId, Long requestId) {
        log.info("Corper {} attempting to cancel clearance request {}", corperId, requestId);

        ClearanceRequest request = getClearanceRequest(requestId);

        // Check ownership
        if (!request.getCorper().getId().equals(corperId)) {
            throw new UnauthorizedException("You cannot cancel another corper's clearance request.");
        }

        if(request.getStatus() != ClearanceStatus.PENDING){
            throw new ApiException(
                    "You can only cancel a PENDING clearance request",
                    HttpStatus.BAD_REQUEST
            );
        }

        request.setStatus(ClearanceStatus.CANCELLED);
        ClearanceRequest savedRequest = clearanceRepo.save(request);

        log.info("Clearance request {} cancelled by Corper {}", requestId, corperId);

        return ClearanceMapper.cancelResponseDto(savedRequest);
    }

    @Override
    public ClearanceResponseDto approveByUnitHead(Long requestId, Long unitHeadId) {
        log.info("UnitHead {} attempting to approve clearance request {}", unitHeadId, requestId);

        ClearanceRequest request = getClearanceRequest(requestId);

        if (request.getStatus() != ClearanceStatus.PENDING) {
            log.warn("Clearance request {} is not PENDING. Current status: {}", requestId, request.getStatus());
            throw new ApiException(
                    "Only PENDING requests can be approved by Unit Head",
                    HttpStatus.BAD_REQUEST
            );
        }

        if(!unitService.isHeadOfCorperUnit(unitHeadId, request.getCorper().getId())){
            throw new UnauthorizedException("You are not authorized to approve this clearance request.");
        }

        request.setStatus(ClearanceStatus.LEVEL_ONE);
        ClearanceRequest savedRequest = clearanceRepo.save(request);

        log.info("Clearance request {} approved by Unit-Head {}", requestId, unitHeadId);
        return ClearanceMapper.toDto(savedRequest);
    }

    @Override
    public RejectClearanceDto rejectByUnitHead(Long requestId, Long unitHeadId, String rejectionReason) {
        log.info("UnitHead {} attempting to reject clearance request {}", unitHeadId, requestId);

        ClearanceRequest request = getClearanceRequest(requestId);

        if (request.getStatus() != ClearanceStatus.PENDING) {
            log.warn("Clearance request {} is not PENDING. Current status: {}", requestId, request.getStatus());
            throw new ApiException(
                    "Only PENDING requests can be rejected by Unit Head",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!unitService.isHeadOfCorperUnit(unitHeadId, request.getCorper().getId())) {
            throw new UnauthorizedException("You are not authorized to reject this clearance request.");
        }

        request.setStatus(ClearanceStatus.REJECTED);
        request.setRejectionReason(rejectionReason);
        request.setRejectedBy(Role.ADMIN);

        ClearanceRequest savedRequest = clearanceRepo.save(request);

        log.info("Clearance request {} rejected by Unit-Head {}", requestId, unitHeadId);
        return ClearanceMapper.rejectResponseDto(savedRequest);
    }


    @Override
    public ClearanceResponseDto approveBySuperAdmin(Long requestId, Long superAdminId) {
        log.info("Super Admin {} attempting to approve clearance request {}", superAdminId, requestId);

        ClearanceRequest request = getClearanceRequest(requestId);

        if (request.getStatus() != ClearanceStatus.LEVEL_ONE) {
            log.warn("Clearance request {} is not LEVEL_ONE. Current status: {}", requestId, request.getStatus());
            throw new ApiException(
                    "Only LEVEL_ONE requests can be approved by Super-Admin",
                    HttpStatus.BAD_REQUEST
            );
        }

        request.setStatus(ClearanceStatus.CLEARED);
        ClearanceRequest savedRequest = clearanceRepo.save(request);

        log.info("Clearance request {} approved by Super-Admin {}", requestId, superAdminId);
        return ClearanceMapper.toDto(savedRequest);
    }

    @Override
    public void deleteAllMyClearanceRequests(Long corperId) {
        int deleted = clearanceRepo.deleteByCorper_Id(corperId);

        if (deleted > 0) {
            log.info("Deleted {} clearance requests for corper {}", deleted, corperId);
        } else {
            log.warn("No clearance requests found for corper {}", corperId);
        }
    }


    @Override
    public RejectClearanceDto rejectBySuperAdmin(Long requestId, Long superAdminId, String rejectionReason) {
        log.info("Super Admin {} attempting to reject clearance request {}", superAdminId, requestId);

        ClearanceRequest request = getClearanceRequest(requestId);

        if (request.getStatus() != ClearanceStatus.LEVEL_ONE) {
            log.warn("Clearance request {} is not LEVEL_ONE. Current status: {}", requestId, request.getStatus());
            throw new ApiException(
                    "Only LEVEL_ONE requests can be rejected by Super-Admin",
                    HttpStatus.BAD_REQUEST
            );
        }

        request.setStatus(ClearanceStatus.REJECTED);
        request.setRejectionReason(rejectionReason);
        request.setRejectedBy(Role.SUPER_ADMIN);

        ClearanceRequest savedRequest = clearanceRepo.save(request);

        log.info("Clearance request {} rejected by Super-Admin {}", requestId, superAdminId);
        return ClearanceMapper.rejectResponseDto(savedRequest);
    }

    @Override
    public List<ClearanceResponseDto> getRequestsForCorper(Long corperId) {

        List<ClearanceRequest> requests = clearanceRepo.findByCorper_Id(corperId);

        log.info("Fetching clearance requests for corper with ID {}", corperId);

        return requests.stream()
                .map(ClearanceMapper::toDto).toList();
    }

    @Override
    public List<ClearanceResponseDto> getRequestsForUnit(Long unitHeadId) {
        Admin unitHead = adminService.getAdminEntityById(unitHeadId);

        Long unitId = unitHead.getUnit().getId();
        log.info("Fetching clearance requests for unit with ID {}", unitId);

        List<ClearanceRequest> requests = clearanceRepo.findAllByUnitId(unitId, Role.ADMIN);

        return requests.stream()
                .map(ClearanceMapper::toDto)
                .toList();
    }

    @Override
    public List<ClearanceResponseDto> getClearanceHistoryForUnit(Long unitHeadId) {
        Admin unitHead = adminService.getAdminEntityById(unitHeadId);

        Long unitId = unitHead.getUnit().getId();
        log.info("Fetching clearance history for unit with ID {}", unitId);

        List<ClearanceRequest> requests = clearanceRepo.findAllByUnitId(unitId, Role.ADMIN);

        return requests.stream()
                .filter(request -> request.getStatus() != ClearanceStatus.PENDING)
                .map(ClearanceMapper::toDto)
                .toList();
    }

    @Override
    public List<ClearanceResponseDto> getNewClearanceRequestsForUnit(Long unitHeadId) {
        Admin unitHead = adminService.getAdminEntityById(unitHeadId);

        Long unitId = unitHead.getUnit().getId();

        log.info("Fetching new clearance requests for unit with ID {}", unitId);

        List<ClearanceRequest> requests = clearanceRepo.findAllByUnitId(unitId, Role.ADMIN);

        return requests.stream()
                .filter(request -> request.getStatus() == ClearanceStatus.PENDING)
                .map(ClearanceMapper::toDto)
                .toList();
    }

    @Override
    public List<ClearanceResponseDto> getAllRequestsForPpa(Long ppaId) {
        Ppa ppa = ppaService.getPpaEntityById(ppaId);

        log.info("Fetching clearance requests for ppa with ID {}", ppaId);

        List<ClearanceRequest> requests = clearanceRepo.findAllByPpaId(ppaId, Role.SUPER_ADMIN);
        return requests.stream()
                .map(ClearanceMapper::toDto)
                .toList();
    }

    @Override
    public List<ClearanceResponseDto> getClearanceHistoryForPpa(Long ppaId) {
        Ppa ppa = ppaService.getPpaEntityById(ppaId);

        log.info("Fetching clearance history for ppa with ID {}", ppaId);

        List<ClearanceRequest> requests = clearanceRepo.findAllByPpaId(ppaId, Role.SUPER_ADMIN);

        return requests.stream()
                .filter(request -> request.getStatus() != ClearanceStatus.LEVEL_ONE)
                .map(ClearanceMapper::toDto)
                .toList();
    }

    @Override
    public List<ClearanceResponseDto> getNewClearanceRequestsForPpa(Long ppaId) {
        Ppa ppa = ppaService.getPpaEntityById(ppaId);

        log.info("Fetching new clearance requests for ppa with ID {}", ppaId);

        List<ClearanceRequest> requests = clearanceRepo.findAllByPpaId(ppaId, Role.SUPER_ADMIN);

        return requests.stream()
                .filter(request -> request.getStatus() == ClearanceStatus.LEVEL_ONE)
                .map(ClearanceMapper::toDto)
                .toList();
    }

    @Override
    public byte[] downloadClearancePDF(Long requestId) {
        ClearanceRequest request = getClearanceRequest(requestId);

        Long corperId = request.getCorper().getId();

        User user = userRepository.findById(corperId)
                .orElseThrow(() -> {
                    log.info("User can not be found with corperId; Trace: Download Clearance PDF");
                    return new ApiException("User does not exist", HttpStatus.BAD_REQUEST);
                });

        ClearanceLetterVariables letterVariables = ClearanceLetterVariables.builder()
                .clearanceLetterDate(request.getCreatedAt())
                .corpsMemberName(user.getFullName())
                .stateCode(request.getCorper().getStateCode())
                .callUpNumber(request.getCorper().getCallUpNumber())
                .clearanceMonthUpper(request.getClearanceMonth())
                .signatoryName("Abiodun Ojo")
                .signatoryTitle("Head Human Resources")
                .build();

        log.info("Starting clearance PDF generation...");

        try {
            // Prepare context
            Context context = new Context();
            context.setVariable("clearanceLetterDate", letterVariables.getClearanceLetterDate());
            context.setVariable("corpsMemberName", letterVariables.getCorpsMemberName());
            context.setVariable("stateCode", letterVariables.getStateCode());
            context.setVariable("callUpNumber", letterVariables.getCallUpNumber());
            context.setVariable("clearanceMonthUpper", letterVariables.getClearanceMonthUpper());
            context.setVariable("signatoryName", letterVariables.getSignatoryName());
            context.setVariable("signatoryTitle", letterVariables.getSignatoryTitle());

            log.debug("Context prepared with corps member details: {}, stateCode: {}, callUpNumber: {}",
                    letterVariables.getCorpsMemberName(),
                    letterVariables.getStateCode(),
                    letterVariables.getCallUpNumber()
            );

            // Generate PDF
            byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml("clearance/clearance-template", context);

            log.info("Clearance PDF generated successfully, size: {} bytes", pdfBytes.length);

            // Build response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment","clearance.pdf");

            return pdfBytes;
        } catch (IOException e) {
            log.error("I/O error while generating clearance PDF", e);
            throw new ApiException("Error writing PDF response: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            log.error("Unexpected error during clearance PDF generation", e);
            throw new ApiException("Error generating clearance PDF: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private void validateMonthAllowed(Month requested) {
        Month current = LocalDate.now().getMonth();
        Month next = current.plus(1); // Month.plus(long)
        if (!(requested.equals(current) || requested.equals(next))) {
            throw new ApiException("You can only request clearance for the current or next month", HttpStatus.BAD_REQUEST);
        }
    }

    public ClearanceRequest getClearanceRequest(Long requestId) {
        return clearanceRepo.findById(requestId)
                .orElseThrow(() -> {
                    log.info("Clearance request with id: {} can not be found.", requestId);
                    return new ResourceNotFoundException("Clearance request with id: {} can not be found.");
                });
    }

    //    @Override
//    public ClearanceRequest resubmitRequest(Long corperId, Long requestId, ClearanceRequestDto dto) {
//        ClearanceRequest req = clearanceRepo.findById(requestId)
//                .orElseThrow(() -> new ResourceNotFoundException("ClearanceRequest: " + requestId));
//
//        if (!req.getCorper().getId().equals(corperId)) throw new ApiException("Forbidden", HttpStatus.FORBIDDEN);
//        if (req.getStatus() != ClearanceStatus.REJECTED) {
//            throw new ApiException("Only rejected requests can be resubmitted", HttpStatus.BAD_REQUEST);
//        }
//
//        validateMonthAllowed(dto.getClearanceMonth());
//        if (dto.getTentativeDate().isBefore(LocalDate.now())) throw new ApiException("Tentative day can't be in the past", HttpStatus.BAD_REQUEST);
//
//        // Option: create new request instead of mutating old (better history)
//        ClearanceRequest newReq = ClearanceRequest.builder()
//                .corper(req.getCorper())
//                .tentativeDate(dto.getTentativeDate())
//                .clearanceMonth(dto.getClearanceMonth())
//                .build();
//
//        return clearanceRepo.save(newReq);
//    }

}

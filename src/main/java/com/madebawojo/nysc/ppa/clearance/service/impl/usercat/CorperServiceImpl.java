package com.madebawojo.nysc.ppa.clearance.service.impl.usercat;

import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
import com.madebawojo.nysc.ppa.clearance.core.exception.ApiException;
import com.madebawojo.nysc.ppa.clearance.core.exception.ResourceNotFoundException;
import com.madebawojo.nysc.ppa.clearance.core.exception.UnauthorizedException;
import com.madebawojo.nysc.ppa.clearance.core.exception.UserAlreadyExistsException;
import com.madebawojo.nysc.ppa.clearance.dto.mapper.CorperMapper;
import com.madebawojo.nysc.ppa.clearance.dto.request.usercat.CorperRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.auth.UpdateCredentialsRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.usercat.CorperResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Corper;
import com.madebawojo.nysc.ppa.clearance.repository.CorperRepository;
import com.madebawojo.nysc.ppa.clearance.repository.PpaRepository;
import com.madebawojo.nysc.ppa.clearance.repository.UnitRepository;
import com.madebawojo.nysc.ppa.clearance.repository.UserRepository;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.EmailVerificationServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.usercat.CorperService;
import com.madebawojo.nysc.ppa.clearance.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CorperServiceImpl implements CorperService {

    private final CorperRepository corperRepository;
    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private final PpaRepository ppaRepository;
    private final EmailVerificationServiceImpl emailVerificationServiceImpl;

    private final PasswordEncoder passwordEncoder;

    @Override
    public CorperResponseDto getCorperById(Long userId) {
        Corper corper = corperRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Corper profile not found for user User ID: " + userId));
        User user = corper.getUser();
        log.info("Corper retrieved successfully. User ID: {}", userId);
        return CorperMapper.toDto(user, corper);
    }

    @Override
    public Corper getCorperEntityById(Long corperId) {
        return  corperRepository.findById(corperId)
                .orElseThrow(() -> new ResourceNotFoundException("Corper profile not found for user User ID: " + corperId));
    }


    @Override
    public List<CorperResponseDto> getAllCorpersInUnit(Long unitId) {
        return corperRepository.findAllByUnitId(unitId).stream()
                .map(corper -> CorperMapper.toDto(corper.getUser(), corper))
                .collect(Collectors.toList());
    }

    @Override
    public List<CorperResponseDto> getAllCorpersInPpa(Long ppaId) {
        return corperRepository.findAllByPpaId(ppaId).stream()
                .map(corper -> CorperMapper.toDto(corper.getUser(), corper))
                .collect(Collectors.toList());
    }


    @Override
    public CorperResponseDto createCorper(CorperRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Corper creation failed — email already in use: {}", request.getEmail());
            throw new UserAlreadyExistsException("Email is already in use");
        }

        if (corperRepository.existsByCallUpNumber(request.getCallUpNumber())) {
            throw new ApiException("A corper with this call-up number already exists", HttpStatus.BAD_REQUEST);
        }

        if (corperRepository.existsByStateCode(request.getStateCode())) {
            throw new ApiException("A corper with this state-corper already exists", HttpStatus.BAD_REQUEST);
        }


        // Create user first
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.CORPER)
//                .isBlocked(false)
                .build();
        userRepository.save(user);
        log.info("User has been created successfully. Attempting to create profile for the user...");

        // Create corper profile
        Corper corper = Corper.builder()
                .user(user)
                .stateCode(request.getStateCode())
                .callUpNumber(request.getCallUpNumber())
                .unit(unitRepository.findById(request.getUnitId()).orElseThrow())
                .ppa(ppaRepository.findById(request.getPpaId()).orElseThrow())
                .isActive(true)
                .build();
        corperRepository.save(corper);
        log.info("Corper user profile has been created successfully");
        emailVerificationServiceImpl.generateAndSendVerificationToken(user);
        return CorperMapper.toDto(user, corper);
    }

    @Override
    public CorperResponseDto updateCorper(Long userId, CorperRequestDto request) {
        Corper corper = corperRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Corper profile not found for User ID: " + userId));

        if (!AppConstants.ALLOW_DIRECT_PROFILE_UPDATE){
            log.warn("Corper with user Id: {} attempted to update profile", userId);
           throw new UnauthorizedException("Profile update not allowed. Send a request to your admin");
        }
        User user = corper.getUser();

        if(request.getFirstName() != null){
            user.setFirstName(request.getFirstName());
            log.info("Corper first name updated to: {}", request.getFirstName());
        }

        if(request.getLastName() != null) {
            user.setLastName(request.getLastName());
            log.info("Corper last name updated to: {}", request.getLastName());
        }

        if(request.getCallUpNumber() != null) {
            corper.setCallUpNumber(request.getCallUpNumber());
            log.info("Corper call-up number name updated to: {}", request.getCallUpNumber());
        }

        if(request.getStateCode() != null) {
            corper.setStateCode(request.getStateCode());
            log.info("Corper state-code updated to: {}", request.getStateCode());
        }

        corperRepository.save(corper);

        log.info("Corper profile updated. User ID: {}", userId);
        return CorperMapper.toDto(user, corper);
    }

    @Override
    public void updateCredentials(String email, UpdateCredentialsRequestDto request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        log.info("Attempting to update credentials for email: {}", email);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Incorrect current password for email: {}", email);
            throw new UnauthorizedException("Incorrect current password");
        }

        if (request.getNewEmail() != null && !request.getNewEmail().isBlank()) {
                if (userRepository.existsByEmail(request.getNewEmail())) {
                    log.warn("Email update failed — already in use: {}", request.getNewEmail());
                    throw new ApiException("Email is already in use", HttpStatus.BAD_REQUEST);
                }
                user.setEmail(request.getNewEmail());
                log.info("Email updated: {} → {}", email, request.getNewEmail());
        }


        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            log.info("Password updated for email: {}", email);
        }

        userRepository.save(user);
        log.info("Credentials successfully updated for email: {}", email);
    }

    @Override
    public void deleteCorper(Long userId) {
        log.warn("Attempting to delete Corper with ID: {}", userId);

        Corper corper = corperRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Corper not found for deletion — ID: {}", userId);
                    return new ResourceNotFoundException("Corper not found");
                });
        corperRepository.delete(corper);
        log.info("Deleted Corper with ID: {}", userId);
    }


    @Override
    public void blockCorper(Long id) {
        Corper corper = corperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corper not found"));
        log.info("Attempting to block corper with user ID: {}", id);

        corper.getUser().setBlocked(true);
        userRepository.save(corper.getUser());
        log.info("Corper has been successfully blocked. user ID: {} ", id);
    }

    @Override
    public void unblockCorper(Long id) {
        Corper corper = corperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corper not found"));
        log.info("Attempting to unblock corper with user ID: {}", id);

        corper.getUser().setBlocked(false);
        userRepository.save(corper.getUser());
        log.info("Corper has been successfully unblocked. user ID: {} ", id);
    }
}


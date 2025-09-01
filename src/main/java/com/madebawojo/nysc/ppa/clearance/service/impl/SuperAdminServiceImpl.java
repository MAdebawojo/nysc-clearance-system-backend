package com.madebawojo.nysc.ppa.clearance.service.impl;

import com.madebawojo.nysc.ppa.clearance.core.exception.UserAlreadyExistsException;
import com.madebawojo.nysc.ppa.clearance.dto.mapper.SuperAdminMapper;
import com.madebawojo.nysc.ppa.clearance.dto.request.SuperAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.UpdateSuperAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.SuperAdminResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.SuperAdmin;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
import com.madebawojo.nysc.ppa.clearance.core.exception.ResourceNotFoundException;
import com.madebawojo.nysc.ppa.clearance.repository.PpaRepository;
import com.madebawojo.nysc.ppa.clearance.repository.SuperAdminRepository;
import com.madebawojo.nysc.ppa.clearance.repository.UserRepository;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.SuperAdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SuperAdminServiceImpl implements SuperAdminService {

    private final SuperAdminRepository superAdminRepository;
    private final UserRepository userRepository;
    private final PpaRepository ppaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SuperAdminResponseDto createSuperAdmin(SuperAdminRequestDto request) {
        log.info("Creating new SuperAdmin with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Super Admin creation failed — email already in use: {}", request.getEmail());
            throw new UserAlreadyExistsException("Email is already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.SUPER_ADMIN)
                .build();
        userRepository.save(user);

        SuperAdmin superAdmin = SuperAdmin.builder()
                .user(user)
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
                .ppa(ppaRepository.findById(request.getPpaId())
                        .orElseThrow(() -> new ResourceNotFoundException("PPA not found with ID: " + request.getPpaId())))
                .build();
        superAdminRepository.save(superAdmin);

        log.info("SuperAdmin created successfully with ID: {}", superAdmin.getId());

        return SuperAdminMapper.toDto(superAdmin);
    }

    @Override
    public List<SuperAdminResponseDto> getAllSuperAdminProfiles() {
        log.info("Fetching all SuperAdmins");

        List<SuperAdminResponseDto> superAdmins = superAdminRepository.findAll().stream()
                .map(SuperAdminMapper::toDto)
                .collect(Collectors.toList());

        log.info("Total super admins retrieved: {}", superAdmins.size());
        return superAdmins;
    }

    @Override
    public SuperAdminResponseDto getSuperAdminProfileById(Long userId) {
        log.info("Fetching SuperAdmin profile for user ID: {}", userId);

        SuperAdmin superAdmin = superAdminRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("SuperAdmin profile not found for user ID: {}", userId);
                    return new ResourceNotFoundException("SuperAdmin profile not found for ID: " + userId);
                });

        return SuperAdminMapper.toDto(superAdmin);
    }

    @Override
    public SuperAdminResponseDto updateSuperAdminProfile(Long userId, UpdateSuperAdminRequestDto request) {
        log.info("Attempting to update SuperAdmin profile for user ID: {}", userId);

        SuperAdmin superAdmin = superAdminRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("SuperAdmin not found for user ID: {}", userId);
                    return new ResourceNotFoundException("SuperAdmin not found for ID: " + userId);
                });

        User user = superAdmin.getUser();

        if (request.getFirstName() != null){
            user.setFirstName(request.getFirstName());
            log.info("Admin first name updated to: {}", request.getFirstName());
        }
        if (request.getLastName() != null){
            user.setLastName(request.getLastName());
            log.info("Admin last name updated to: {}", request.getLastName());
        }
        if (request.getEmail() != null){
            user.setEmail(request.getEmail());
            log.info("Admin email updated to: {}", request.getEmail());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            log.info("Admin email updated");
        }

        userRepository.save(user);
        log.info("SuperAdmin profile updated successfully for user ID: {}", userId);

        return SuperAdminMapper.toDto(superAdmin);
    }

    @Override
    public void deleteSuperAdmin(Long superAdminId) {
        log.warn("Attempting to delete SuperAdmin with ID: {}", superAdminId);

        SuperAdmin superAdmin = superAdminRepository.findById(superAdminId)
                .orElseThrow(() -> {
                    log.warn("SuperAdmin not found for deletion — ID: {}", superAdminId);
                    return new ResourceNotFoundException("SuperAdmin not found for ID: " + superAdminId);
                });

        superAdminRepository.delete(superAdmin);
        log.info("SuperAdmin with ID: {} has been deleted successfully", superAdminId);
    }
}

package com.madebawojo.nysc.ppa.clearance.service.impl.usercat;

import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
import com.madebawojo.nysc.ppa.clearance.core.exception.ResourceNotFoundException;
import com.madebawojo.nysc.ppa.clearance.core.exception.UserAlreadyExistsException;
import com.madebawojo.nysc.ppa.clearance.dto.mapper.AdminMapper;
import com.madebawojo.nysc.ppa.clearance.dto.request.usercat.AdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.usercat.UpdateAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.usercat.AdminResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.ppa.Unit;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Admin;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.SuperAdmin;
import com.madebawojo.nysc.ppa.clearance.repository.AdminRepository;
import com.madebawojo.nysc.ppa.clearance.repository.PpaRepository;
import com.madebawojo.nysc.ppa.clearance.repository.UnitRepository;
import com.madebawojo.nysc.ppa.clearance.repository.UserRepository;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.EmailVerificationServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.impl.auth.PasswordSetupServiceImpl;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.usercat.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final UnitRepository unitRepository;
    private final PpaRepository ppaRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationServiceImpl emailVerificationServiceImpl;
    private final SuperAdminServiceImpl superAdminService;
    private final PasswordSetupServiceImpl passwordSetupService;


    @Override
    public AdminResponseDto createAdmin(Long superAdminId, AdminRequestDto request) {
        log.info("Attempting to create new admin with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Admin creation failed — Email already exists: {}", request.getEmail());
            throw new UserAlreadyExistsException("Email is already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.ADMIN)
//                .isBlocked(false)
                .build();
        userRepository.save(user);

        log.info("User account created successfully for admin: {}", request.getEmail());

        SuperAdmin superAdmin = superAdminService.getSuperAdminEntityById(superAdminId);

//      Admin admin = Admin.builder()
//                .user(user)
//                .unit(unitRepository.findById(request.getUnitId())
//                        .orElseThrow(() -> new ResourceNotFoundException("Unit not found with ID: " + request.getUnitId())))
//                .ppa(ppaRepository.findById(request.getPpaId())
//                        .orElseThrow(() -> new ResourceNotFoundException("PPA not found with ID: " + request.getPpaId())))
//                .build();

        Admin admin = Admin.builder()
                .user(user)
                .unit(unitRepository.findById(request.getUnitId())
                        .orElseThrow(() -> new ResourceNotFoundException("Unit not found with ID: " + request.getUnitId())))
                .ppa(superAdmin.getPpa())
                .build();

        adminRepository.save(admin);

        log.info("Admin profile created successfully for user: {}", user.getEmail());

        passwordSetupService.requestPasswordSetup(user);

//        emailVerificationServiceImpl.generateAndSendVerificationToken(user);

        return AdminMapper.toDto(user, admin);
    }

    @Override
    public AdminResponseDto getAdminById(Long userId) {
        log.info("Fetching admin profile for user ID: {}", userId);
        Admin admin = adminRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Admin profile not found for user ID: {}", userId);
                    return new ResourceNotFoundException("Admin profile not found for user ID: " + userId);
                });
        log.info("Admin profile retrieved successfully for user ID: {}", userId);
        return AdminMapper.toDto(admin.getUser(), admin);
    }

    @Override
    public Admin getAdminEntityById(Long userId) {
        log.info("Fetching admin profile for user ID: {}", userId);
        Admin admin = adminRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Admin profile not found for user ID: {}", userId);
                    return new ResourceNotFoundException("Admin profile not found for user ID: " + userId);
                });
        log.info("Admin profile retrieved successfully for user ID: {}", userId);
        return admin;
    }

    @Override
    public Long getUnitIdByAdmin(Long userId) {
        log.info("Fetching unit ID for admin with user ID: {}", userId);
        Admin admin = adminRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Admin not found for user ID: {}", userId);
                    return new ResourceNotFoundException("Admin not found for user ID: " + userId);
                });
        Long unitId = admin.getUnit().getId();
        log.info("Unit ID {} retrieved for admin with user ID: {}", unitId, userId);
        return unitId;
    }

    @Override
    public List<AdminResponseDto> getAllAdmins() {
        log.info("Fetching list of all admins");
        List<AdminResponseDto> admins = adminRepository.findAll().stream()
                .map(admin -> AdminMapper.toDto(admin.getUser(), admin))
                .collect(Collectors.toList());
        log.info("Total admins retrieved: {}", admins.size());
        return admins;
    }

    @Override
    public AdminResponseDto updateAdmin(Long adminId, UpdateAdminRequestDto request) {
        log.info("Attempting to update admin profile with ID: {}", adminId);
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> {
                    log.warn("Admin profile not found for ID: {}", adminId);
                    return new ResourceNotFoundException("Admin profile not found with ID: " + adminId);
                });

        User user = admin.getUser();

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Email already in use: {}", request.getEmail());
                throw new UserAlreadyExistsException("Email already in use");
            }
            user.setEmail(request.getEmail());
            log.info("Admin email updated to: {}", request.getEmail());
        }

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            log.info("Admin password updated.");
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
            log.info("Admin first name updated to: {}", request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
            log.info("Admin last name updated to: {}", request.getLastName());
        }

        userRepository.save(user);
        adminRepository.save(admin);
        log.info("Admin profile with ID: {} updated successfully", adminId);

        return AdminMapper.toDto(admin.getUser(), admin);
    }

    @Override
    public void deleteAdmin(Long adminId) {
        log.warn("Attempting to delete admin with ID: {}", adminId);
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> {
                    log.warn("Admin profile not found for deletion — ID: {}", adminId);
                    return new ResourceNotFoundException("Admin profile not found for ID: " + adminId);
                });

        adminRepository.delete(admin);
        log.info("Admin with ID: {} has been deleted successfully", adminId);
    }
}


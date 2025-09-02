package com.madebawojo.nysc.ppa.clearance.service.impl.auth;

import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
import com.madebawojo.nysc.ppa.clearance.core.exception.ApiException;
import com.madebawojo.nysc.ppa.clearance.core.exception.ResourceNotFoundException;
import com.madebawojo.nysc.ppa.clearance.core.exception.UnauthorizedException;
import com.madebawojo.nysc.ppa.clearance.dto.request.UpdateCredentialsRequestDto;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import com.madebawojo.nysc.ppa.clearance.repository.UserRepository;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth.UserService;
import com.madebawojo.nysc.ppa.clearance.util.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String updateCredentials(String email, UpdateCredentialsRequestDto request) {

        User user = findUserByEmail(email);

        validateOldPassword(request.getOldPassword(), user.getPassword());

       boolean passwordUpdated= updatePasswordIfPresent(user, request.getNewPassword(), email);
       boolean emailUpdated =  updateEmailIfPresent(user, request.getNewEmail(), email);

        userRepository.save(user);
        log.info("Credentials updated for user with email: {}", email);

        if (passwordUpdated && emailUpdated) {
            return "Password and email updated successfully";
        } else if (passwordUpdated) {
            return "Password updated successfully";
        } else if (emailUpdated) {
            return "Email updated successfully";
        } else {
            return "No updates were made";
        }
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.info("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
    }

    private void validateOldPassword(String inputPassword, String storedHash) {
        if (!passwordEncoder.matches(inputPassword, storedHash)) {
            throw new UnauthorizedException("Incorrect current password");
        }
    }

    private boolean updatePasswordIfPresent(User user, String newPassword, String email) {
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
            log.info("Password updated for email: {}", email);
            return true;
        }
        return false;
    }

    private boolean updateEmailIfPresent(User user, String newEmail, String currentEmail) {
        if (newEmail != null && !newEmail.isBlank()) {
            if (!AppConstants.CAN_CORPERS_UPDATE_EMAIL && user.getRole() == Role.CORPER) {
                log.warn("Corper with email: {}, attempted to update email.", currentEmail);
                return false;
            }

            if (userRepository.existsByEmail(newEmail)) {
                throw new ApiException("Email is already in use", HttpStatus.BAD_REQUEST);
            }

            user.setEmail(newEmail);
            log.info("Email updated: {} → {}", currentEmail, newEmail);
            return true;
        }
        return false;
    }

}

















//package com.madebawojo.nysc.ppa.clearance.service.impl;
//
//import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
//import com.madebawojo.nysc.ppa.clearance.core.exception.ApiException;
//import com.madebawojo.nysc.ppa.clearance.core.exception.ResourceNotFoundException;
//import com.madebawojo.nysc.ppa.clearance.core.exception.UnauthorizedException;
//import com.madebawojo.nysc.ppa.clearance.dto.request.UpdateCredentialsRequestDto;
//import com.madebawojo.nysc.ppa.clearance.entity.user.User;
//import com.madebawojo.nysc.ppa.clearance.repository.UserRepository;
//import com.madebawojo.nysc.ppa.clearance.service.servicecontract.auth.UserService;
//import com.madebawojo.nysc.ppa.clearance.util.AppConstants;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class UserServiceImpl implements UserService {
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void updateCredentials(String email, UpdateCredentialsRequestDto request) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> {
//                    log.info("User not found with email: {}", email);
//                    return new ResourceNotFoundException("User not found with email: " + email);
//                });
//
//        log.warn("{} is attempting to update Credentials", email);
//
//        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
//            log.warn("Incorrect current password for email: {}", email);
//            throw new UnauthorizedException("Incorrect current password");
//        }
//
//        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
//            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//            log.info("Password updated for email: {}", email);
//        }
//
//        if (request.getNewEmail() != null && !request.getNewEmail().isBlank()) {
//            if (!AppConstants.CAN_CORPERS_UPDATE_EMAIL && user.getRole() == Role.CORPER){
//                log.warn("Corper with email: {}, attempted to update email.", email);
//                throw new UnauthorizedException("Corpers are not allowed to update email");
//            }
//
//            if (userRepository.existsByEmail(request.getNewEmail())) {
//                log.warn("Email update failed — already in use: {}", request.getNewEmail());
//                throw new ApiException("Email is already in use", HttpStatus.BAD_REQUEST);
//            }
//            user.setEmail(request.getNewEmail());
//            log.info("Email updated: {} → {}", email, request.getNewEmail());
//        }
//
//        userRepository.save(user);
//        log.info("Email updated: {} → {}", email, request.getNewEmail());
//    }
//
//}
//


















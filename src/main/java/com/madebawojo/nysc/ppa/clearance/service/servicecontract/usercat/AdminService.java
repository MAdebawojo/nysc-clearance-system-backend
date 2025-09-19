package com.madebawojo.nysc.ppa.clearance.service.servicecontract.usercat;

import com.madebawojo.nysc.ppa.clearance.dto.request.usercat.AdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.usercat.UpdateAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.usercat.AdminResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Admin;

import java.util.List;


public interface AdminService {
    AdminResponseDto createAdmin(Long userId, AdminRequestDto request);
    AdminResponseDto getAdminById(Long userId);
    Admin getAdminEntityById(Long userId);
    List<AdminResponseDto> getAllAdmins();
    Long getUnitIdByAdmin(Long userId);
    AdminResponseDto updateAdmin(Long adminId, UpdateAdminRequestDto request);
    void deleteAdmin(Long adminId);

}

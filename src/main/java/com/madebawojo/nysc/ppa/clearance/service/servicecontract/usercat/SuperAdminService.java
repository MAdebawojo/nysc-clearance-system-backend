package com.madebawojo.nysc.ppa.clearance.service.servicecontract.usercat;

import com.madebawojo.nysc.ppa.clearance.dto.request.usercat.SuperAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.usercat.UpdateSuperAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.usercat.SuperAdminResponseDto;

import java.util.List;

public interface SuperAdminService {
    SuperAdminResponseDto getSuperAdminProfileById(Long userId);
    SuperAdminResponseDto updateSuperAdminProfile(Long userId, UpdateSuperAdminRequestDto request);
    void deleteSuperAdmin(Long superAdminId);
    SuperAdminResponseDto createSuperAdmin(SuperAdminRequestDto request);
    List<SuperAdminResponseDto> getAllSuperAdminProfiles();
}



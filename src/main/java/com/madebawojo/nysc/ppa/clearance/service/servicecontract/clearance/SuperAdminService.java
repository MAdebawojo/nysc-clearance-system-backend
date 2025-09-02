package com.madebawojo.nysc.ppa.clearance.service.servicecontract.clearance;

import com.madebawojo.nysc.ppa.clearance.dto.request.SuperAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.UpdateSuperAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.SuperAdminResponseDto;

import java.util.List;

public interface SuperAdminService {
    SuperAdminResponseDto getSuperAdminProfileById(Long userId);
    SuperAdminResponseDto updateSuperAdminProfile(Long userId, UpdateSuperAdminRequestDto request);
    void deleteSuperAdmin(Long superAdminId);
    SuperAdminResponseDto createSuperAdmin(SuperAdminRequestDto request);
    List<SuperAdminResponseDto> getAllSuperAdminProfiles();
}



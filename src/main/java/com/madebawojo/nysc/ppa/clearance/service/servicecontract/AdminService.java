package com.madebawojo.nysc.ppa.clearance.service.servicecontract;

import com.madebawojo.nysc.ppa.clearance.dto.request.AdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.UpdateAdminRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.AdminResponseDto;

import java.util.List;


public interface AdminService {
    AdminResponseDto createAdmin(AdminRequestDto request);
    AdminResponseDto getAdminById(Long userId);
    List<AdminResponseDto> getAllAdmins();
    Long getUnitIdByAdmin(Long userId);
    AdminResponseDto updateAdmin(Long adminId, UpdateAdminRequestDto request);
    void deleteAdmin(Long adminId);

}

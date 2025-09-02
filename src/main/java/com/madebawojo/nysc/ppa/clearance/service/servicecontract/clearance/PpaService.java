package com.madebawojo.nysc.ppa.clearance.service.servicecontract.clearance;

import com.madebawojo.nysc.ppa.clearance.dto.request.PpaRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.UpdatePpaRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.PpaResponseDto;

import java.util.List;

public interface PpaService {
    PpaResponseDto createPpa(PpaRequestDto dto);
    List<PpaResponseDto> getAllPpas();
    PpaResponseDto getPpaById(Long id);
    PpaResponseDto updatePpa(Long id, UpdatePpaRequestDto dto);
    void deletePpa(Long id);
}

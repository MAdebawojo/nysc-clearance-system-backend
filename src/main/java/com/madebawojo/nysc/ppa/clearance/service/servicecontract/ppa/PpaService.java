package com.madebawojo.nysc.ppa.clearance.service.servicecontract.ppa;

import com.madebawojo.nysc.ppa.clearance.dto.request.ppa.PpaRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.ppa.UpdatePpaRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.ppa.PpaResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.ppa.Ppa;

import java.util.List;

public interface PpaService {
    PpaResponseDto createPpa(PpaRequestDto dto);
    List<PpaResponseDto> getAllPpas();
    PpaResponseDto getPpaById(Long id);
    Ppa getPpaEntityById(Long id);
    PpaResponseDto updatePpa(Long id, UpdatePpaRequestDto dto);
    void deletePpa(Long id);
}

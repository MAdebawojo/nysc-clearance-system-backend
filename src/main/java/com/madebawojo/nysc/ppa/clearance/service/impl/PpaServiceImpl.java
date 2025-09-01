package com.madebawojo.nysc.ppa.clearance.service.impl;

import com.madebawojo.nysc.ppa.clearance.core.exception.ResourceNotFoundException;
import com.madebawojo.nysc.ppa.clearance.dto.mapper.PpaMapper;
import com.madebawojo.nysc.ppa.clearance.dto.request.PpaRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.request.UpdatePpaRequestDto;
import com.madebawojo.nysc.ppa.clearance.dto.response.PpaResponseDto;
import com.madebawojo.nysc.ppa.clearance.entity.Ppa;
import com.madebawojo.nysc.ppa.clearance.repository.PpaRepository;
import com.madebawojo.nysc.ppa.clearance.service.servicecontract.PpaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PpaServiceImpl implements PpaService {

    private final PpaRepository ppaRepository;

    @Override
    public PpaResponseDto createPpa(PpaRequestDto dto) {
        Ppa ppa = Ppa.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .build();
        ppaRepository.save(ppa);
        log.info("Created new PPA: {}", ppa.getName());
        return PpaMapper.toDto(ppa);
    }

    @Override
    public List<PpaResponseDto> getAllPpas() {
        return ppaRepository.findAll()
                .stream()
                .map(PpaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PpaResponseDto getPpaById(Long id) {
        Ppa ppa = ppaRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("PPA with ID {} not found", id);
                        return new ResourceNotFoundException("PPA not found");
                    });
        log.info("PPA retrieved successfully. ID: {}", id);
        return PpaMapper.toDto(ppa);
    }


    @Override
    public PpaResponseDto updatePpa(Long id, UpdatePpaRequestDto dto) {
        log.warn("Attempting to update a PPA");

        Ppa ppa = ppaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PPA not found"));

        if (dto.getPpaName() != null) {
            ppa.setName(dto.getPpaName());
            log.info("PPA name updated to: {}", dto.getPpaName());
        }

        if (dto.getPpaAddress() != null) {
            ppa.setAddress(dto.getPpaAddress());
            log.info("PPA address updated to: {}", dto.getPpaAddress());
        }

        ppaRepository.save(ppa);
        log.info("Updated PPA: {}", ppa.getName());

        return PpaMapper.toDto(ppa);
    }

    @Override
    public void deletePpa(Long id) {
        log.warn("Attempting to delete PPA with ID: {}", id);

        Ppa ppa = ppaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("PPA not found for deletion — ID: {}", id);
                    return new ResourceNotFoundException("PPA not found");
                });
        ppaRepository.delete(ppa);
        log.info("Deleted PPA with ID: {}", id);
    }
}

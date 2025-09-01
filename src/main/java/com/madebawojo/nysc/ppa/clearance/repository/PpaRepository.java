package com.madebawojo.nysc.ppa.clearance.repository;

import com.madebawojo.nysc.ppa.clearance.entity.Ppa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PpaRepository extends JpaRepository<Ppa, Long> {
    Optional<Ppa> findById(Long id);
}

package com.madebawojo.nysc.ppa.clearance.repository;

import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUserId(Long userId);
}

package com.madebawojo.nysc.ppa.clearance.repository;

import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Admin;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Long> {
    Optional<SuperAdmin> findByUserId(Long userId);
    boolean existsByPpaId(Long ppaId);
}

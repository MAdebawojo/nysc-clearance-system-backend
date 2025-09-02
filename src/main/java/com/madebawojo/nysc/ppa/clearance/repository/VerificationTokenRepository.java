package com.madebawojo.nysc.ppa.clearance.repository;

import com.madebawojo.nysc.ppa.clearance.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByTokenHash(String token);
}

package com.madebawojo.nysc.ppa.clearance.repository;

import com.madebawojo.nysc.ppa.clearance.entity.auth.VerificationToken;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    void deleteByUser(User user);
    Optional<VerificationToken> findByTokenHash(String token);
}

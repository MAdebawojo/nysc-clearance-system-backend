package com.madebawojo.nysc.ppa.clearance.repository;

import com.madebawojo.nysc.ppa.clearance.entity.PasswordResetToken;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiresAt < :now")
    int deleteAllExpiredSince(@Param("now") LocalDateTime now);

    void deleteByUser(User user);

    Optional<PasswordResetToken> findByTokenHash(String token);
}

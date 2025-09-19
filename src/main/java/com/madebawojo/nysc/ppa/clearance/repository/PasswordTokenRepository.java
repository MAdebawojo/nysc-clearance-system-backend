package com.madebawojo.nysc.ppa.clearance.repository;

import com.madebawojo.nysc.ppa.clearance.core.enums.PasswordTokenType;
import com.madebawojo.nysc.ppa.clearance.entity.auth.PasswordToken;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {
//    @Modifying
//    @Query("DELETE FROM PasswordResetToken t WHERE t.expiresAt < :now")
//    int deleteAllExpiredSince(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM PasswordToken pt WHERE pt.user = :user AND pt.type = :expectedType")
    void deleteByUserAndType(@Param("user") User user, @Param("expectedType") PasswordTokenType expectedType);

    Optional<PasswordToken> findByTokenHashAndType(String token, PasswordTokenType tokenType);
}

package com.madebawojo.nysc.ppa.clearance.entity;

import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_rt_user", columnList = "user_id"),
        @Index(name = "idx_rt_family", columnList = "familyId")
})
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 128)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Rotation family identifier to detect reuse
    @Column(nullable = false, length = 64)
    private String familyId;

    // Optional chain info (for audits / reuse detection)
    private Long replacedById;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    private Instant revokedAt;
    private String revokeReason;

    // context
    private String userAgent;
    private String ip;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}

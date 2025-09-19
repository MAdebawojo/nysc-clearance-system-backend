package com.madebawojo.nysc.ppa.clearance.entity.clearance;

import com.madebawojo.nysc.ppa.clearance.core.enums.ClearanceStatus;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Corper;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Month;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clearance_requests")
public class ClearanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = true)
    private LocalDate tentativeDate;

    @Enumerated(EnumType.STRING)
    private Month clearanceMonth;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ClearanceStatus status = ClearanceStatus.PENDING;

    @Column(nullable = true)
    private String rejectionReason;

    @Column(nullable = true)
    private String cancellationReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corper_id", nullable = false)
    private Corper corper; // Assuming you already have a User entity

    @Builder.Default
    private LocalDate createdAt = LocalDate.now();

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ppa_id", nullable = false)
//    private Ppa ppa;

}


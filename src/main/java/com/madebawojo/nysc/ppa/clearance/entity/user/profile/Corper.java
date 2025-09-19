package com.madebawojo.nysc.ppa.clearance.entity.user.profile;

import com.madebawojo.nysc.ppa.clearance.entity.clearance.ClearanceRequest;
import com.madebawojo.nysc.ppa.clearance.entity.ppa.Ppa;
import com.madebawojo.nysc.ppa.clearance.entity.ppa.Unit;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import jakarta.persistence.*;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "corpers")
public class Corper {
    @Id
    private Long id;

    @OneToOne
    @MapsId // Ensures that corper ID is the same as user ID (hence a corper with ID 1 is the person with user ID 1)
    @JoinColumn(name = "id", nullable = false)
    private User user;

//    @Column(nullable = false)
//    private String firstName;
//
//    @Column(nullable = false)
//    private String lastName;

    @Column(unique = true, nullable = false)
    private String stateCode;

    @Column(unique = true, nullable = false)
    private String callUpNumber;

    @Column(nullable = false)
    private boolean isActive;

    /* Entity relationships */
    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    @ToString.Exclude
    private Unit unit;

    @ManyToOne
    @JoinColumn(name = "ppa_id", nullable = false)
    @ToString.Exclude
    private Ppa ppa;

    @Builder.Default
    @OneToMany(mappedBy = "corper", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClearanceRequest> clearanceRequests = new ArrayList<>();

}

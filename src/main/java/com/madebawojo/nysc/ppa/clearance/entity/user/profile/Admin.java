package com.madebawojo.nysc.ppa.clearance.entity.user.profile;

import com.madebawojo.nysc.ppa.clearance.entity.Ppa;
import com.madebawojo.nysc.ppa.clearance.entity.Unit;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import jakarta.persistence.*;
import jakarta.persistence.Table;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "admins")
public class Admin {
    @Id
    private Long id;

    @OneToOne
    @MapsId // Ensures that Admin ID is the same as user ID (hence an Admin with ID 1 is the person with user ID 1)
    @JoinColumn(name = "id", nullable = false)
    private User user;

//    @Column(nullable = false)
//    private String firstName;
//
//    @Column(nullable = false)
//    private String lastName;

    /* Entity relationships */
    @OneToOne
    @JoinColumn(name = "unit_id", nullable = false)
    @ToString.Exclude
    private Unit unit;

    @ManyToOne
    @JoinColumn(name = "ppa_id", nullable = false)
    @ToString.Exclude
    private Ppa ppa;
}

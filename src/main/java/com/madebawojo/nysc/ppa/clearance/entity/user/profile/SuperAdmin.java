package com.madebawojo.nysc.ppa.clearance.entity.user.profile;

import com.madebawojo.nysc.ppa.clearance.entity.Ppa;
import com.madebawojo.nysc.ppa.clearance.entity.Unit;
import com.madebawojo.nysc.ppa.clearance.entity.user.User;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "super_admins")
public class SuperAdmin {
    @Id
    private Long id;

    @OneToOne
    @MapsId // Ensures that Admin ID is the same as user ID (hence an Admin with ID 1 is the person with user ID 1)
    @JoinColumn(name = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    /* Entity relationship */
    @ManyToOne
    @JoinColumn(name = "ppa_id", nullable = false)
    private Ppa ppa;
}

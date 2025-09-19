package com.madebawojo.nysc.ppa.clearance.entity.ppa;

import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Admin;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Corper;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.SuperAdmin;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ppas")
public class Ppa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "PPA name is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 255)
    private String address;

    /* Relationships */

    @OneToMany(mappedBy = "ppa", cascade = CascadeType.ALL, orphanRemoval = true)     // One PPA can have multiple units
    @ToString.Exclude
    private List<Unit> units;

//    @OneToMany(mappedBy = "ppa", cascade = CascadeType.ALL, orphanRemoval = true)     // One PPA can have multiple super-admins
    @OneToOne(mappedBy = "ppa", cascade = CascadeType.ALL, orphanRemoval = true)
//    @ToString.Exclude
    private SuperAdmin superAdmin;
//    private List<SuperAdmin> superAdmins;

    @OneToMany(mappedBy = "ppa", cascade = CascadeType.ALL, orphanRemoval = true)     // One PPA can have multiple admins
    @ToString.Exclude
    private List<Admin> admins;

    // One PPA can have multiple corpers
    @OneToMany(mappedBy = "ppa", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Corper> corpers;

//    @Builder.Default
//    @OneToMany(mappedBy = "ppa", cascade = CascadeType.ALL, orphanRemoval = true)
//    @ToString.Exclude
//    private List<ClearanceRequest> clearanceRequests = new ArrayList<>();

}

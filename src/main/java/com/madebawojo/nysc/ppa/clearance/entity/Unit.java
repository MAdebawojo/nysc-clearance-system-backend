package com.madebawojo.nysc.ppa.clearance.entity;

import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Admin;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Corper;
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
@Table(name = "units")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Unit name is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "ppa_id", nullable = false)
    private Ppa ppa;

    /* Relationships */
    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)     // A Unit can have multiple corpers
    @ToString.Exclude
    private List<Corper> corpers;

    @OneToOne(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)     // A Unit has only one unit-head
    private Admin unitHead;
}


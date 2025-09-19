package com.madebawojo.nysc.ppa.clearance.entity.user;

import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
import com.madebawojo.nysc.ppa.clearance.entity.auth.PasswordToken;
import com.madebawojo.nysc.ppa.clearance.entity.auth.VerificationToken;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Admin;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Corper;
import com.madebawojo.nysc.ppa.clearance.entity.user.profile.SuperAdmin;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Builder.Default
    @Column
    private String password = null;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    @Column(nullable = false)
    private boolean isBlocked = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean isVerified = false;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VerificationToken> verificationTokens = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PasswordToken> passwordTokens = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.role != null ? List.of(new SimpleGrantedAuthority("ROLE_" + role.name())) : List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
//        return true;
        return this.isVerified && !this.isBlocked;
    }

    /**
     * A Helper function used in UserService to validate that a user can only have one role at anytime during creation/update
    **/
    public boolean hasValidProfileForRole() {
        return switch (this.role) {
            case CORPER -> this.corper != null && this.admin == null && this.superAdmin == null;
            case ADMIN -> this.admin != null && this.corper == null && this.superAdmin == null;
            case SUPER_ADMIN -> this.superAdmin != null && this.corper == null && this.admin == null;
            default -> false;
        };
    }

    public String getFullName(){
        return this.firstName + " " + this.lastName;
    }

    /* Relationships to profiles */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Corper corper;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Admin admin;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SuperAdmin superAdmin;
}


























//import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.List;
//
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table(name="_user")
//public class User implements UserDetails {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;
//
//    private String firstName;
//
//    private String lastName;
//
//    private String email;
//
//    private String password;
//
//    @Enumerated(EnumType.STRING)
//    private Role role;
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(role.name()));
//    }
//
//    @Override
//    public String getPassword() {
//        return this.password;
//    }
//
//    @Override
//    public String getUsername() {
//        return this.email;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}
//

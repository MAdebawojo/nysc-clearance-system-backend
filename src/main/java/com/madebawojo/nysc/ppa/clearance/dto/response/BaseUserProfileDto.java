package com.madebawojo.nysc.ppa.clearance.dto.response;

import com.madebawojo.nysc.ppa.clearance.core.enums.Role;

/**
 * Contains data returned when a user successfully authenticates*/
public class BaseUserProfileDto {
    private String email;
    private String firstName;
    private String lastName;
    private String ppaName;
    private String token;
    private Role role;
}

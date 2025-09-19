package com.madebawojo.nysc.ppa.clearance.dto.request.usercat;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuperAdminRequestDto {

    @Email(message = "Invalid email address")
    @NotBlank
    private String email;

    @NotBlank(message = "Super-Admin password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    @JsonProperty("first_name")
    @NotBlank(message = "Super-Admin first name is required")
    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters long.")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "Super-Admin last name is required")
    @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters long.")
    private String lastName;

    @JsonProperty("ppa_id")
    @Column(unique = true)
    @NotNull(message = "PPA assignment is required")
    private Long ppaId;
}

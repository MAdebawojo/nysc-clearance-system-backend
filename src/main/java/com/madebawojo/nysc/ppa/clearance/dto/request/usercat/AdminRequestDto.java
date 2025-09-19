package com.madebawojo.nysc.ppa.clearance.dto.request.usercat;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminRequestDto {

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private String email;

//    @NotBlank(message = "Admin password is required")
//    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
//    private String password;

    @JsonProperty("first_name")
    @NotBlank(message = "Admin password first-name is required")
    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters long.")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "Admin password last-name is required")
    @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters long.")
    private String lastName;

//    @JsonProperty("ppa_id")
//    @NotNull(message = "PPA assignment is required")
//    private Long ppaId;

    @JsonProperty("unit_id")
    @NotNull(message = "Unit assignment is required")
    private Long unitId;
}

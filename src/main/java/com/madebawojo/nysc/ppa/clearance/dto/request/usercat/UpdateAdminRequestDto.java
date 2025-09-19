package com.madebawojo.nysc.ppa.clearance.dto.request.usercat;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAdminRequestDto {
    @Email(message = "Invalid email address")
    private String email;
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
}

package com.madebawojo.nysc.ppa.clearance.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UpdateSuperAdminRequestDto {
    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    private String firstName;
    private String lastName;
}

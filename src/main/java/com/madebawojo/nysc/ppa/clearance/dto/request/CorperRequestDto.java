package com.madebawojo.nysc.ppa.clearance.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CorperRequestDto {

    @Email(message = "Invalid email address")
    @NotBlank(message = "Corper email is required")
    private String email;

    @NotBlank(message = "Corper password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    @NotBlank(message = "Corper first-name is required")
    private String firstName;

    @NotBlank(message = "Corper last-name is required")
    private String lastName;

    @NotBlank(message = "Corper state-code is required")
    private String stateCode;

    @NotBlank(message = "Corper call-up number is required")
    private String callUpNumber;

    @NotNull(message = "PPA assignment is required")
    private Long ppaId;

    @NotNull(message = "Unit assignment is required")
    private Long unitId;
}

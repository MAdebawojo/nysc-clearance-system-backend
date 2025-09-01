package com.madebawojo.nysc.ppa.clearance.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCredentialsRequestDto {
    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String newPassword;

    @Email(message = "Invalid email format")
    private String newEmail;
}

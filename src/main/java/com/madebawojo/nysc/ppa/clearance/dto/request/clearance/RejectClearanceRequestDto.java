package com.madebawojo.nysc.ppa.clearance.dto.request.clearance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RejectClearanceRequestDto {
    @NotBlank(message = "Reason is required")
    @Size(max = 100, message = "Reason should not exceed 100 characters")
    private String reason;
}

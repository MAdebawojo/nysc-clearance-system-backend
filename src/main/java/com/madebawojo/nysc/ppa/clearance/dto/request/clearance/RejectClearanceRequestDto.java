package com.madebawojo.nysc.ppa.clearance.dto.request.clearance;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectClearanceRequestDto {
    @NotBlank(message = "Reason is required")
    private String reason;
}

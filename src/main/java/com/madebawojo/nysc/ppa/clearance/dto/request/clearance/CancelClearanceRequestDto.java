package com.madebawojo.nysc.ppa.clearance.dto.request.clearance;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CancelClearanceRequestDto {
    @JsonProperty("request_id")
    @NotNull(message = "Request ID is required")
    private Long id;

    @JsonProperty("cancellation_reason")
    @NotBlank(message = "Provide a reason for your cancellation")
    private String cancellationReason;
}

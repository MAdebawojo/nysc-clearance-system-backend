package com.madebawojo.nysc.ppa.clearance.dto.response.clearance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.madebawojo.nysc.ppa.clearance.core.enums.ClearanceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.Month;

@Data
@Builder
public class ClearanceResponseDto {
    @JsonProperty("request_id")
    private Long id;
    @JsonProperty("corper_id")
    private Long corperId;
    @JsonProperty("corper_name")
    private String corperName;
    @JsonProperty("tentative_date")
    private LocalDate tentativeDate;
    @JsonProperty("clearance_month")
    private Month clearanceMonth;
    private ClearanceStatus status;
    @JsonProperty("rejection_reason")
    private String rejectionReason;
}

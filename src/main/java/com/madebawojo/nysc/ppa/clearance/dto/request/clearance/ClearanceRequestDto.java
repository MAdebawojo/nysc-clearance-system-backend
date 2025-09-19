package com.madebawojo.nysc.ppa.clearance.dto.request.clearance;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.Month;

@Data
public class ClearanceRequestDto {

    @JsonProperty("tentative_date")
    @NotNull(message = "Tentative clearance date is required")
    @FutureOrPresent(message = "Clearance date must be today or in the future")
    private LocalDate tentativeDate;

    @JsonProperty("clearance_month")
    @NotNull(message = "Clearance month is required")
    private Month clearanceMonth;
}


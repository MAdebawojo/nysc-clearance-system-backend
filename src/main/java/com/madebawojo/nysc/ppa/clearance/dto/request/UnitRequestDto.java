package com.madebawojo.nysc.ppa.clearance.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UnitRequestDto {

    @NotBlank(message = "Unit name is required")
    @Size(min = 2, max = 100)
    private String name;

    private Long ppaId;
}

package com.madebawojo.nysc.ppa.clearance.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UpdatePpaRequestDto {

    @Size(min = 2, max = 100)
    private String ppaName;

    @Size(max = 255)
    private String ppaAddress;

}

package com.madebawojo.nysc.ppa.clearance.util;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.Month;

@Data
@Builder
public class ClearanceLetterVariables {
    private LocalDate clearanceLetterDate;
    private String corpsMemberName;
    private String stateCode;
    private String callUpNumber;
    private Month clearanceMonthUpper;
    private String signatoryName;
    private String signatoryTitle;
}

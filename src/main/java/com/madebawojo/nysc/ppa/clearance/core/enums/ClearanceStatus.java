package com.madebawojo.nysc.ppa.clearance.core.enums;

public enum ClearanceStatus {
    PENDING,     // when corper submits
    LEVEL_ONE,   // approved by unit head
    CLEARED,     // fully cleared
    REJECTED,     // rejected at any level
    CANCELLED     // cancel a clearance request by corper
}

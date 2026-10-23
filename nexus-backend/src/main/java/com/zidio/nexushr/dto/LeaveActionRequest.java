package com.zidio.nexushr.dto;

import com.zidio.nexushr.entity.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveActionRequest {

    @NotNull(message = "Status is required (APPROVED or REJECTED)")
    private LeaveStatus status;

    private String remarks; // e.g. "Approved. Ensure work handover."
}
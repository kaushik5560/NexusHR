package com.zidio.nexushr.dto;

import com.zidio.nexushr.entity.LeaveType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveApplyRequest {

    @NotNull(message = "Leave type is required (CASUAL, SICK, ANNUAL, UNPAID)")
    private LeaveType leaveType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotBlank(message = "Reason for leave is required")
    private String reason;
}
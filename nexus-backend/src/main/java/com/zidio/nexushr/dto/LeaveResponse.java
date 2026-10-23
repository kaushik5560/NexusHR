package com.zidio.nexushr.dto;

import com.zidio.nexushr.entity.LeaveRequest;
import com.zidio.nexushr.entity.LeaveStatus;
import com.zidio.nexushr.entity.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LeaveResponse {
    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalDays;
    private String reason;
    private LeaveStatus status;
    private String reviewedByName;
    private String managerRemarks;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;

    public static LeaveResponse fromEntity(LeaveRequest leave) {
        String reviewer = (leave.getReviewedBy() != null)
                ? leave.getReviewedBy().getFirstName() + " " + leave.getReviewedBy().getLastName()
                : null;

        return LeaveResponse.builder()
                .id(leave.getId())
                .employeeId(leave.getEmployee().getId())
                .employeeCode(leave.getEmployee().getEmployeeCode())
                .employeeName(leave.getEmployee().getFirstName() + " " + leave.getEmployee().getLastName())
                .leaveType(leave.getLeaveType())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .totalDays(leave.getTotalDays())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .reviewedByName(reviewer)
                .managerRemarks(leave.getManagerRemarks())
                .appliedAt(leave.getAppliedAt())
                .reviewedAt(leave.getReviewedAt())
                .build();
    }
}
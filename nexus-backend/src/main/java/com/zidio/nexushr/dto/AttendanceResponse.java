package com.zidio.nexushr.dto;

import com.zidio.nexushr.entity.Attendance;
import com.zidio.nexushr.entity.AttendanceStatus;
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
public class AttendanceResponse {
    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private LocalDate date;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Double workHours;
    private AttendanceStatus status;
    private String notes;

    public static AttendanceResponse fromEntity(Attendance att) {
        return AttendanceResponse.builder()
                .id(att.getId())
                .employeeId(att.getEmployee().getId())
                .employeeCode(att.getEmployee().getEmployeeCode())
                .employeeName(att.getEmployee().getFirstName() + " " + att.getEmployee().getLastName())
                .date(att.getDate())
                .checkInTime(att.getCheckInTime())
                .checkOutTime(att.getCheckOutTime())
                .workHours(att.getWorkHours())
                .status(att.getStatus())
                .notes(att.getNotes())
                .build();
    }
}
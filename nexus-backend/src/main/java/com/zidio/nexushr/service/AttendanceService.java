package com.zidio.nexushr.service;

import com.zidio.nexushr.dto.AttendanceResponse;
import com.zidio.nexushr.entity.Attendance;
import com.zidio.nexushr.entity.AttendanceStatus;
import com.zidio.nexushr.entity.Employee;
import com.zidio.nexushr.repository.AttendanceRepository;
import com.zidio.nexushr.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    // 1. Employee Daily Check-in
    @Transactional
    public AttendanceResponse checkIn(String email, String notes) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + email));

        LocalDate today = LocalDate.now();

        if (attendanceRepository.existsByEmployeeAndDate(employee, today)) {
            throw new IllegalArgumentException("You have already checked in for today (" + today + ")!");
        }

        LocalDateTime now = LocalDateTime.now();
        // Shift starts at 9:30 AM. After that, mark as LATE
        AttendanceStatus status = now.toLocalTime().isAfter(LocalTime.of(9, 30))
                ? AttendanceStatus.LATE
                : AttendanceStatus.PRESENT;

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .date(today)
                .checkInTime(now)
                .status(status)
                .notes(notes)
                .build();

        Attendance saved = attendanceRepository.save(attendance);
        return AttendanceResponse.fromEntity(saved);
    }

    // 2. Employee Daily Check-out & Hours Calculation
    @Transactional
    public AttendanceResponse checkOut(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + email));

        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeAndDate(employee, today)
                .orElseThrow(() -> new IllegalArgumentException("No check-in record found for today! Please check in first."));

        if (attendance.getCheckOutTime() != null) {
            throw new IllegalArgumentException("You have already checked out for today!");
        }

        LocalDateTime now = LocalDateTime.now();
        attendance.setCheckOutTime(now);

        // Calculate hours worked
        long minutesWorked = Duration.between(attendance.getCheckInTime(), now).toMinutes();
        double hoursWorked = Math.round((minutesWorked / 60.0) * 100.0) / 100.0;
        attendance.setWorkHours(hoursWorked);

        // If worked less than 4 hours, mark as HALF_DAY
        if (hoursWorked < 4.0 && attendance.getStatus() != AttendanceStatus.LATE) {
            attendance.setStatus(AttendanceStatus.HALF_DAY);
        }

        Attendance updated = attendanceRepository.save(attendance);
        return AttendanceResponse.fromEntity(updated);
    }

    // 3. Get Today's Status for Logged-in Employee
    @Transactional(readOnly = true)
    public AttendanceResponse getTodayAttendance(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + email));

        return attendanceRepository.findByEmployeeAndDate(employee, LocalDate.now())
                .map(AttendanceResponse::fromEntity)
                .orElse(null);
    }

    // 4. Get Past Attendance History for Logged-in Employee
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getMyAttendanceHistory(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + email));

        return attendanceRepository.findByEmployeeOrderByDateDesc(employee).stream()
                .map(AttendanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 5. Get All Attendance by Date (HR / Admin Dashboard)
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAllAttendanceByDate(LocalDate date) {
        LocalDate queryDate = (date != null) ? date : LocalDate.now();
        return attendanceRepository.findByDate(queryDate).stream()
                .map(AttendanceResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
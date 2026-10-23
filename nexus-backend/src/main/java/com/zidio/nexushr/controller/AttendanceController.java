package com.zidio.nexushr.controller;

import com.zidio.nexushr.dto.AttendanceResponse;
import com.zidio.nexushr.dto.MessageResponse;
import com.zidio.nexushr.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // 1. POST: /api/attendance/check-in - Employee daily check-in
    @PostMapping("/check-in")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> checkIn(Authentication authentication, @RequestBody(required = false) Map<String, String> body) {
        try {
            String email = authentication.getName();
            String notes = (body != null) ? body.get("notes") : null;
            AttendanceResponse response = attendanceService.checkIn(email, notes);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // 2. POST: /api/attendance/check-out - Employee daily check-out
    @PostMapping("/check-out")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> checkOut(Authentication authentication) {
        try {
            String email = authentication.getName();
            AttendanceResponse response = attendanceService.checkOut(email);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // 3. GET: /api/attendance/today - Get today's attendance status for logged-in user
    @GetMapping("/today")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getTodayAttendance(Authentication authentication) {
        String email = authentication.getName();
        AttendanceResponse response = attendanceService.getTodayAttendance(email);
        if (response == null) {
            return ResponseEntity.ok(new MessageResponse("Not checked in yet today."));
        }
        return ResponseEntity.ok(response);
    }

    // 4. GET: /api/attendance/my-records - Past attendance records for logged-in user
    @GetMapping("/my-records")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AttendanceResponse>> getMyAttendanceHistory(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(attendanceService.getMyAttendanceHistory(email));
    }

    // 5. GET: /api/attendance/all - HR/Admin dashboard attendance
    @GetMapping("/all")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AttendanceResponse>> getAllAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAllAttendanceByDate(date));
    }
}
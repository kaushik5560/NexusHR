package com.zidio.nexushr.controller;

import com.zidio.nexushr.dto.LeaveActionRequest;
import com.zidio.nexushr.dto.LeaveApplyRequest;
import com.zidio.nexushr.dto.LeaveResponse;
import com.zidio.nexushr.dto.MessageResponse;
import com.zidio.nexushr.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // 1. POST: /api/leaves/apply - Employee applies for leave
    @PostMapping("/apply")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> applyLeave(Authentication authentication, @Valid @RequestBody LeaveApplyRequest request) {
        try {
            String email = authentication.getName();
            LeaveResponse response = leaveService.applyLeave(email, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // 2. GET: /api/leaves/my-leaves - Current employee's leave applications
    @GetMapping("/my-leaves")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LeaveResponse>> getMyLeaves(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(leaveService.getMyLeaves(email));
    }

    // 3. GET: /api/leaves/pending - Pending leaves queue for Managers & HR
    @GetMapping("/pending")
    @PreAuthorize("hasRole('HR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<LeaveResponse>> getPendingLeaves() {
        return ResponseEntity.ok(leaveService.getPendingLeaves());
    }

    // 4. GET: /api/leaves/all - Company-wide leaves history
    @GetMapping("/all")
    @PreAuthorize("hasRole('HR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<LeaveResponse>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    // 5. PUT: /api/leaves/{id}/process - Approve or Reject a leave application
    @PutMapping("/{id}/process")
    @PreAuthorize("hasRole('HR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> processLeave(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody LeaveActionRequest action) {
        try {
            String reviewerEmail = authentication.getName();
            LeaveResponse response = leaveService.processLeave(id, reviewerEmail, action);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
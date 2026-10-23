package com.zidio.nexushr.controller;

import com.zidio.nexushr.dto.EmployeeResponse;
import com.zidio.nexushr.dto.EmployeeUpdateRequest;
import com.zidio.nexushr.dto.MessageResponse;
import com.zidio.nexushr.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // 1. GET: /api/employees - Saare employees ki list
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // 2. GET: /api/employees/me - Logged-in user ka apna profile (JWT Token se)
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmployeeResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(employeeService.getMyProfile(email));
    }

    // 3. GET: /api/employees/{id} - Specific employee by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(employeeService.getEmployeeById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // 4. PUT: /api/employees/{id} - Employee profile update
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody EmployeeUpdateRequest request) {
        try {
            return ResponseEntity.ok(employeeService.updateEmployee(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // 5. DELETE: /api/employees/{id} - Soft Delete / Offboard Employee
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> terminateEmployee(@PathVariable Long id) {
        try {
            employeeService.terminateEmployee(id);
            return ResponseEntity.ok(new MessageResponse("Employee offboarded/terminated successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
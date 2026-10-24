package com.zidio.nexushr.controller;

import com.zidio.nexushr.dto.MessageResponse;
import com.zidio.nexushr.dto.PayrollGenerateRequest;
import com.zidio.nexushr.dto.PayrollResponse;
import com.zidio.nexushr.service.PayrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    // 1. POST: /api/payroll/generate - HR Generates Monthly Payroll
    @PostMapping("/generate")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<?> generatePayroll(@Valid @RequestBody PayrollGenerateRequest request) {
        try {
            PayrollResponse response = payrollService.generatePayroll(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // 2. GET: /api/payroll/my-payslips - Current Employee's Payslip History
    @GetMapping("/my-payslips")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PayrollResponse>> getMyPayslips(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(payrollService.getMyPayslips(email));
    }

    // 3. GET: /api/payroll/{id} - Single Payslip Details
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPayslipById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(payrollService.getPayslipById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // 4. GET: /api/payroll/all - Company Monthly Payroll (HR View)
    @GetMapping("/all")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<List<PayrollResponse>> getAllPayrolls(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(payrollService.getAllPayrolls(month, year));
    }
}
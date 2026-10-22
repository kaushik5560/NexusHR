package com.zidio.nexushr.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    // 1. Public Endpoint - Koi bhi access kar sakta hai bina token ke
    @GetMapping("/all")
    public Map<String, String> allAccess() {
        return Map.of("message", "Public Content: Anyone can access this without a token.");
    }

    // 2. Protected Endpoint - Sirf valid Employee/HR/Manager/Admin access kar sakte hain
    @GetMapping("/employee")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('HR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public Map<String, String> employeeAccess() {
        return Map.of("message", "Employee Dashboard: Access granted with valid Employee token.");
    }

    // 3. HR Protected Endpoint - Sirf HR aur Admin access kar sakte hain
    @GetMapping("/hr")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public Map<String, String> hrAccess() {
        return Map.of("message", "HR Portal: Access granted to HR Specialist.");
    }

    // 4. Admin Protected Endpoint - Sirf ADMIN access kar sakta hai
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> adminAccess() {
        return Map.of("message", "Admin Console: High-privilege Admin access granted.");
    }
}
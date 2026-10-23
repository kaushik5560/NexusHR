package com.zidio.nexushr.service;

import com.zidio.nexushr.dto.EmployeeResponse;
import com.zidio.nexushr.dto.EmployeeUpdateRequest;
import com.zidio.nexushr.entity.Employee;
import com.zidio.nexushr.entity.EmployeeStatus;
import com.zidio.nexushr.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // 1. Get All Employees
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 2. Get Single Employee by ID
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));
        return EmployeeResponse.fromEntity(employee);
    }

    // 3. Get Currently Logged-in Employee Profile (via JWT email)
    @Transactional(readOnly = true)
    public EmployeeResponse getMyProfile(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + email));
        return EmployeeResponse.fromEntity(employee);
    }

    // 4. Update Employee Details
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));

        if (request.getFirstName() != null) employee.setFirstName(request.getFirstName());
        if (request.getLastName() != null) employee.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) employee.setPhoneNumber(request.getPhoneNumber());
        if (request.getDepartment() != null) employee.setDepartment(request.getDepartment());
        if (request.getDesignation() != null) employee.setDesignation(request.getDesignation());
        if (request.getSalary() != null) employee.setSalary(request.getSalary());
        if (request.getStatus() != null) employee.setStatus(request.getStatus());

        Employee updated = employeeRepository.save(employee);
        return EmployeeResponse.fromEntity(updated);
    }

    // 5. Offboard / Terminate Employee (Enterprise Soft Delete)
    @Transactional
    public void terminateEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));
        
        employee.setStatus(EmployeeStatus.TERMINATED);
        employeeRepository.save(employee);
    }
}
package com.zidio.nexushr.dto;

import com.zidio.nexushr.entity.Employee;
import com.zidio.nexushr.entity.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String department;
    private String designation;
    private BigDecimal salary;
    private EmployeeStatus status;
    private LocalDate hireDate;
    private List<String> roles;
    private LocalDateTime createdAt;

    // Entity ko Response DTO me convert karne ka method
    public static EmployeeResponse fromEntity(Employee emp) {
        return EmployeeResponse.builder()
                .id(emp.getId())
                .employeeCode(emp.getEmployeeCode())
                .firstName(emp.getFirstName())
                .lastName(emp.getLastName())
                .email(emp.getEmail())
                .phoneNumber(emp.getPhoneNumber())
                .department(emp.getDepartment())
                .designation(emp.getDesignation())
                .salary(emp.getSalary())
                .status(emp.getStatus())
                .hireDate(emp.getHireDate())
                .roles(emp.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()))
                .createdAt(emp.getCreatedAt())
                .build();
    }
}
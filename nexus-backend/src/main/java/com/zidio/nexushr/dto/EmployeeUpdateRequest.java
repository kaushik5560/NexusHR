package com.zidio.nexushr.dto;

import com.zidio.nexushr.entity.EmployeeStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EmployeeUpdateRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String department;
    private String designation;
    private BigDecimal salary;
    private EmployeeStatus status;
}
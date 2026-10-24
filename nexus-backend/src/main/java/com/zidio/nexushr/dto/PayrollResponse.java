package com.zidio.nexushr.dto;

import com.zidio.nexushr.entity.Payroll;
import com.zidio.nexushr.entity.PayrollStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PayrollResponse {
    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private String designation;
    private int month;
    private int year;
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal specialAllowance;
    private BigDecimal grossSalary;
    private BigDecimal pfDeduction;
    private BigDecimal taxDeduction;
    private BigDecimal totalDeductions;
    private BigDecimal netSalary;
    private PayrollStatus status;
    private LocalDate paymentDate;
    private String payslipReference;
    private LocalDateTime createdAt;

    public static PayrollResponse fromEntity(Payroll p) {
        return PayrollResponse.builder()
                .id(p.getId())
                .employeeId(p.getEmployee().getId())
                .employeeCode(p.getEmployee().getEmployeeCode())
                .employeeName(p.getEmployee().getFirstName() + " " + p.getEmployee().getLastName())
                .department(p.getEmployee().getDepartment())
                .designation(p.getEmployee().getDesignation())
                .month(p.getMonth())
                .year(p.getYear())
                .basicSalary(p.getBasicSalary())
                .hra(p.getHra())
                .specialAllowance(p.getSpecialAllowance())
                .grossSalary(p.getGrossSalary())
                .pfDeduction(p.getPfDeduction())
                .taxDeduction(p.getTaxDeduction())
                .totalDeductions(p.getTotalDeductions())
                .netSalary(p.getNetSalary())
                .status(p.getStatus())
                .paymentDate(p.getPaymentDate())
                .payslipReference(p.getPayslipReference())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
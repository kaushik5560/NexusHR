package com.zidio.nexushr.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payrolls", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "month", "year"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Associated Employee
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private int month; // e.g. 10 for October

    @Column(nullable = false)
    private int year;  // e.g. 2026

    // Earnings Breakdown
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal hra; // 40% of Basic

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal specialAllowance; // 20% of Basic

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal grossSalary; // Basic + HRA + Allowance

    // Deductions Breakdown
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal pfDeduction; // 12% of Basic (Provident Fund)

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal taxDeduction; // Income Tax / TDS

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDeductions;

    // Final Take-Home
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal netSalary; // Gross - Total Deductions

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private PayrollStatus status = PayrollStatus.PROCESSED;

    private LocalDate paymentDate;

    @Column(unique = true, nullable = false, length = 50)
    private String payslipReference; // e.g. PAY-202610-EMP1001

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
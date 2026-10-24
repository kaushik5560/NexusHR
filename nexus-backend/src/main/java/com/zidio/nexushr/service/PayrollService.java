package com.zidio.nexushr.service;

import com.zidio.nexushr.dto.PayrollGenerateRequest;
import com.zidio.nexushr.dto.PayrollResponse;
import com.zidio.nexushr.entity.Employee;
import com.zidio.nexushr.entity.Payroll;
import com.zidio.nexushr.entity.PayrollStatus;
import com.zidio.nexushr.repository.EmployeeRepository;
import com.zidio.nexushr.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    // 1. Generate Monthly Payroll for Employee (HR / Admin Action)
    @Transactional
    public PayrollResponse generatePayroll(PayrollGenerateRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + request.getEmployeeId()));

        if (payrollRepository.existsByEmployeeAndMonthAndYear(employee, request.getMonth(), request.getYear())) {
            throw new IllegalArgumentException("Payroll already generated for this employee for month " 
                    + request.getMonth() + "/" + request.getYear());
        }

        // Base salary from employee record (fallback to 50000 if not set)
        BigDecimal baseSalary = (employee.getSalary() != null && employee.getSalary().compareTo(BigDecimal.ZERO) > 0)
                ? employee.getSalary()
                : BigDecimal.valueOf(50000.00);

        // Earnings Calculation
        BigDecimal basicSalary = baseSalary.multiply(BigDecimal.valueOf(0.50)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal hra = basicSalary.multiply(BigDecimal.valueOf(0.40)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal specialAllowance = basicSalary.multiply(BigDecimal.valueOf(0.20)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal grossSalary = basicSalary.add(hra).add(specialAllowance);

        // Deductions Calculation
        BigDecimal pfDeduction = basicSalary.multiply(BigDecimal.valueOf(0.12)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxDeduction = (grossSalary.compareTo(BigDecimal.valueOf(50000)) > 0)
                ? grossSalary.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP)
                : grossSalary.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalDeductions = pfDeduction.add(taxDeduction);
        BigDecimal netSalary = grossSalary.subtract(totalDeductions);

        // Generate clean unique reference: PAY-202610-EMP1001
        String cleanEmpCode = employee.getEmployeeCode().replace("-", "");
        String reference = String.format("PAY-%d%02d-%s", request.getYear(), request.getMonth(), cleanEmpCode);

        Payroll payroll = Payroll.builder()
                .employee(employee)
                .month(request.getMonth())
                .year(request.getYear())
                .basicSalary(basicSalary)
                .hra(hra)
                .specialAllowance(specialAllowance)
                .grossSalary(grossSalary)
                .pfDeduction(pfDeduction)
                .taxDeduction(taxDeduction)
                .totalDeductions(totalDeductions)
                .netSalary(netSalary)
                .status(PayrollStatus.PAID)
                .paymentDate(LocalDate.now())
                .payslipReference(reference)
                .build();

        Payroll saved = payrollRepository.save(payroll);
        return PayrollResponse.fromEntity(saved);
    }

    // 2. Get All Payslips for Logged-in Employee (Self-Service)
    @Transactional(readOnly = true)
    public List<PayrollResponse> getMyPayslips(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + email));

        return payrollRepository.findByEmployeeOrderByYearDescMonthDesc(employee).stream()
                .map(PayrollResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 3. Get Single Payslip by ID
    @Transactional(readOnly = true)
    public PayrollResponse getPayslipById(Long id) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll record not found with id: " + id));
        return PayrollResponse.fromEntity(payroll);
    }

    // 4. Get Company-wide Payroll by Month & Year (HR Dashboard)
    @Transactional(readOnly = true)
    public List<PayrollResponse> getAllPayrolls(int month, int year) {
        return payrollRepository.findByMonthAndYear(month, year).stream()
                .map(PayrollResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
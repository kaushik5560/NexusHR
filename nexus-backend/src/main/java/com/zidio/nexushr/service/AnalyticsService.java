package com.zidio.nexushr.service;

import com.zidio.nexushr.dto.WorkforceAnalyticsResponse;
import com.zidio.nexushr.entity.*;
import com.zidio.nexushr.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final PerformanceReviewRepository reviewRepository;
    private final PayrollRepository payrollRepository;

    @Transactional(readOnly = true)
    public WorkforceAnalyticsResponse getWorkforceAnalytics() {
        List<Employee> employees = employeeRepository.findAll();
        int totalEmployees = employees.size();

        // 1. Department Distribution
        Map<String, Long> departmentHeadcount = employees.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDepartment() != null ? e.getDepartment() : "General",
                        Collectors.counting()
                ));

        // 2. Monthly Payroll Outflow
        double totalSalaryOutflow = employees.stream()
                .mapToDouble(e -> e.getSalary() != null ? e.getSalary().doubleValue() : 75000.0)
                .sum();

        // 3. Average Performance Score
        List<PerformanceReview> reviews = reviewRepository.findAll();
        double avgPerformance = reviews.isEmpty() ? 4.5 :
                reviews.stream().mapToDouble(PerformanceReview::getOverallScore).average().orElse(4.5);
        avgPerformance = Math.round(avgPerformance * 10.0) / 10.0;

        // 4. Today's Attendance Distribution
        List<Attendance> todayAttendance = attendanceRepository.findByDate(LocalDate.now());
        Map<String, Long> attendanceCounts = new HashMap<>();
        attendanceCounts.put("PRESENT", todayAttendance.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count());
        attendanceCounts.put("LATE", todayAttendance.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count());
        attendanceCounts.put("HALF_DAY", todayAttendance.stream().filter(a -> a.getStatus() == AttendanceStatus.HALF_DAY).count());
        attendanceCounts.put("ABSENT", todayAttendance.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count());

        double complianceRate = totalEmployees == 0 ? 100.0 :
                Math.round(((double) todayAttendance.size() / totalEmployees * 100.0) * 10.0) / 10.0;

        // 5. Leaves Statistics
        List<LeaveRequest> allLeaves = leaveRepository.findAll();
        long pendingLeaves = allLeaves.stream().filter(l -> l.getStatus() == LeaveStatus.PENDING).count();
        long approvedLeaves = allLeaves.stream().filter(l -> l.getStatus() == LeaveStatus.APPROVED).count();
        double approvalRate = allLeaves.isEmpty() ? 100.0 :
                Math.round(((double) approvedLeaves / allLeaves.size() * 100.0) * 10.0) / 10.0;

        // 6. Retention Health Index (calculated from performance + leave stability)
        double retentionHealth = Math.min(96.0, Math.max(72.0, (avgPerformance / 5.0 * 50.0) + (approvalRate * 0.4)));
        retentionHealth = Math.round(retentionHealth * 10.0) / 10.0;

        return WorkforceAnalyticsResponse.builder()
                .totalEmployees(totalEmployees)
                .monthlyPayrollOutflow(totalSalaryOutflow)
                .averagePerformanceScore(avgPerformance)
                .retentionHealthScore(retentionHealth)
                .attendanceComplianceRate(complianceRate)
                .departmentHeadcount(departmentHeadcount)
                .attendanceStatusCounts(attendanceCounts)
                .pendingLeavesCount(pendingLeaves)
                .approvedLeavesCount(approvedLeaves)
                .leaveApprovalRate(approvalRate)
                .build();
    }
}

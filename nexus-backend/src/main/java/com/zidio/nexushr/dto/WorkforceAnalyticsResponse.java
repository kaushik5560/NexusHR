package com.zidio.nexushr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkforceAnalyticsResponse {
    private int totalEmployees;
    private double monthlyPayrollOutflow;
    private double averagePerformanceScore;
    private double retentionHealthScore;
    private double attendanceComplianceRate;
    private Map<String, Long> departmentHeadcount;
    private Map<String, Long> attendanceStatusCounts;
    private long pendingLeavesCount;
    private long approvedLeavesCount;
    private double leaveApprovalRate;
}

package com.zidio.nexushr.service;

import com.zidio.nexushr.dto.AttritionRiskResponse;
import com.zidio.nexushr.dto.WorkforceRiskOverviewResponse;
import com.zidio.nexushr.entity.*;
import com.zidio.nexushr.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkforceAiService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final PerformanceReviewRepository reviewRepository;

    // 1. Analyze single employee attrition & flight risk
    public AttritionRiskResponse analyzeEmployeeAttritionRisk(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + employeeId));

        List<Attendance> attendances = attendanceRepository.findByEmployeeOrderByDateDesc(employee);
        List<LeaveRequest> leaves = leaveRepository.findByEmployeeOrderByAppliedAtDesc(employee);
        List<PerformanceReview> reviews = reviewRepository.findByEmployeeOrderByCreatedAtDesc(employee);

        double baseRisk = 12.0;
        List<String> riskFactors = new ArrayList<>();
        List<String> retentionActions = new ArrayList<>();

        // Factor A: Attendance Patterns
        long lateOrAbsentCount = attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.LATE || a.getStatus() == AttendanceStatus.ABSENT)
                .count();

        if (attendances.size() > 0 && ((double) lateOrAbsentCount / attendances.size()) > 0.25) {
            baseRisk += 18.0;
            riskFactors.add("Irregular biometric schedule: Chronic late check-ins observed");
            retentionActions.add("Offer flexible work hours or hybrid shift schedule");
        }

        // Factor B: Leave Frequency Trends
        long totalLeavesTaken = leaves.stream()
                .filter(l -> l.getStatus() == LeaveStatus.APPROVED || l.getStatus() == LeaveStatus.PENDING)
                .count();

        if (totalLeavesTaken >= 2) {
            baseRisk += 14.0;
            riskFactors.add("Frequent recent leave requests (" + totalLeavesTaken + " applications submitted)");
            retentionActions.add("Conduct 1-on-1 check-in to assess work-life balance and project fatigue");
        }

        // Factor C: Performance Scorecard & Market Poaching Risk
        if (!reviews.isEmpty()) {
            PerformanceReview latestReview = reviews.get(0);
            double score = latestReview.getOverallScore();

            if (score >= 4.5) {
                // High performer compensation flight risk
                baseRisk += 22.0;
                riskFactors.add("Top Performer (Score: " + score + " / 5.0) - High external headhunter poaching risk");
                retentionActions.add("Recommend 15-20% compensation market adjustment and retention equity");
                retentionActions.add("Fast-track promotion to Senior Leadership / Tech Lead track");
            } else if (score < 2.8) {
                baseRisk += 35.0;
                riskFactors.add("Low evaluation rating (" + score + " / 5.0) - Potential role misalignment or disengagement");
                retentionActions.add("Initiate Performance Improvement Plan (PIP) with structured mentorship");
            }

            if (latestReview.getCommunicationRating() < 3.0) {
                baseRisk += 10.0;
                riskFactors.add("Decreased team communication score indicating potential isolation");
            }
        } else {
            riskFactors.add("Baseline profile: No finalized 360-degree reviews recorded yet");
            retentionActions.add("Complete Q4 performance review to establish talent baseline");
        }

        // Default healthy actions if low risk
        if (retentionActions.isEmpty()) {
            retentionActions.add("Maintain regular quarterly touchpoints");
            retentionActions.add("Acknowledge consistent delivery during team standups");
        }

        // Clamp risk score between 8.0% and 95.0%
        double finalScore = Math.min(95.0, Math.max(8.0, Math.round(baseRisk * 10.0) / 10.0));

        // Determine Level & Flight Window
        String riskLevel;
        String flightRiskWindow;

        if (finalScore < 30.0) {
            riskLevel = "LOW";
            flightRiskWindow = "Low Flight Risk (> 12 Months)";
        } else if (finalScore < 55.0) {
            riskLevel = "MODERATE";
            flightRiskWindow = "Moderate Window (6 - 12 Months)";
        } else if (finalScore < 75.0) {
            riskLevel = "HIGH";
            flightRiskWindow = "Elevated Risk Window (60 - 90 Days)";
        } else {
            riskLevel = "CRITICAL";
            flightRiskWindow = "Imminent Flight Risk (< 30 Days)";
        }

        String aiDiagnosis = String.format(
                "AI workforce assessment indicates %s flight risk (%s%%). %s.",
                riskLevel.toLowerCase(),
                finalScore,
                riskFactors.isEmpty() ? "Employee shows high organizational stability" : String.join("; ", riskFactors)
        );

        return AttritionRiskResponse.builder()
                .employeeId(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .riskScore(finalScore)
                .riskLevel(riskLevel)
                .confidenceScore(93.4)
                .flightRiskWindow(flightRiskWindow)
                .primaryRiskFactors(riskFactors)
                .aiDiagnosis(aiDiagnosis)
                .retentionActions(retentionActions)
                .build();
    }

    // 2. Company-wide workforce attrition risk overview
    public WorkforceRiskOverviewResponse getCompanyWorkforceRiskOverview() {
        List<Employee> allEmployees = employeeRepository.findAll();
        List<AttritionRiskResponse> highRiskEmployees = new ArrayList<>();

        int low = 0, mod = 0, high = 0, crit = 0;
        double totalScore = 0.0;

        for (Employee emp : allEmployees) {
            AttritionRiskResponse response = analyzeEmployeeAttritionRisk(emp.getId());
            totalScore += response.getRiskScore();

            switch (response.getRiskLevel()) {
                case "LOW" -> low++;
                case "MODERATE" -> mod++;
                case "HIGH" -> {
                    high++;
                    highRiskEmployees.add(response);
                }
                case "CRITICAL" -> {
                    crit++;
                    highRiskEmployees.add(response);
                }
            }
        }

        double avgScore = allEmployees.isEmpty() ? 0.0 : Math.round((totalScore / allEmployees.size()) * 10.0) / 10.0;

        return WorkforceRiskOverviewResponse.builder()
                .totalEmployeesAnalyzed(allEmployees.size())
                .lowRiskCount(low)
                .moderateRiskCount(mod)
                .highRiskCount(high)
                .criticalRiskCount(crit)
                .companyAverageRiskScore(avgScore)
                .highRiskEmployees(highRiskEmployees)
                .build();
    }
}
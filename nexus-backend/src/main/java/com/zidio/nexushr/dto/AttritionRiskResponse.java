package com.zidio.nexushr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttritionRiskResponse {
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private String designation;
    
    private Double riskScore;            // 0.0 to 100.0%
    private String riskLevel;            // LOW, MODERATE, HIGH, CRITICAL
    private Double confidenceScore;      // e.g. 92.5%
    private String flightRiskWindow;     // e.g. "Low Risk (> 12 Months)", "High Risk (30-60 Days)"
    
    private List<String> primaryRiskFactors;
    private String aiDiagnosis;
    private List<String> retentionActions;
}
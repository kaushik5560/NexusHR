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
public class WorkforceRiskOverviewResponse {
    private int totalEmployeesAnalyzed;
    private int lowRiskCount;
    private int moderateRiskCount;
    private int highRiskCount;
    private int criticalRiskCount;
    private double companyAverageRiskScore;
    private List<AttritionRiskResponse> highRiskEmployees;
}
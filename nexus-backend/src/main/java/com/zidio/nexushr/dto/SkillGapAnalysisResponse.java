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
public class SkillGapAnalysisResponse {
    private Long employeeId;
    private String employeeName;
    private String currentRole;
    private String targetRole;
    private Double readinessScore;          // e.g. 74.0%
    private String readinessLevel;          // "High Readiness", "Developing", "Early Stage"
    private List<String> verifiedSkills;
    private List<String> identifiedSkillGaps;
    private List<CareerMilestone> learningPath;
    private String aiCareerAdvice;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CareerMilestone {
        private String timeframe;           // "Month 1 (Days 1-30)", "Month 2 (Days 31-60)"
        private String title;
        private String focusArea;
        private String recommendedAction;
    }
}
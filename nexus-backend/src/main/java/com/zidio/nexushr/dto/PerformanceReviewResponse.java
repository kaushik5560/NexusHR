package com.zidio.nexushr.dto;

import com.zidio.nexushr.entity.PerformanceReview;
import com.zidio.nexushr.entity.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PerformanceReviewResponse {
    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private String designation;
    private String reviewerName;
    private String reviewPeriod;
    private Double technicalSkillsRating;
    private Double communicationRating;
    private Double productivityRating;
    private Double leadershipRating;
    private Double overallScore;
    private String keyAchievements;
    private String areasOfImprovement;
    private String managerFeedback;
    private ReviewStatus status;
    private LocalDateTime createdAt;

    public static PerformanceReviewResponse fromEntity(PerformanceReview r) {
        String reviewer = r.getReviewer().getFirstName() + " " + r.getReviewer().getLastName();
        return PerformanceReviewResponse.builder()
                .id(r.getId())
                .employeeId(r.getEmployee().getId())
                .employeeCode(r.getEmployee().getEmployeeCode())
                .employeeName(r.getEmployee().getFirstName() + " " + r.getEmployee().getLastName())
                .department(r.getEmployee().getDepartment())
                .designation(r.getEmployee().getDesignation())
                .reviewerName(reviewer)
                .reviewPeriod(r.getReviewPeriod())
                .technicalSkillsRating(r.getTechnicalSkillsRating())
                .communicationRating(r.getCommunicationRating())
                .productivityRating(r.getProductivityRating())
                .leadershipRating(r.getLeadershipRating())
                .overallScore(r.getOverallScore())
                .keyAchievements(r.getKeyAchievements())
                .areasOfImprovement(r.getAreasOfImprovement())
                .managerFeedback(r.getManagerFeedback())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
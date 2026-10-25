package com.zidio.nexushr.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceReviewRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotBlank(message = "Review period is required (e.g. Q3 2026)")
    private String reviewPeriod;

    @NotNull(message = "Technical skills rating is required (1.0 - 5.0)")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private Double technicalSkillsRating;

    @NotNull(message = "Communication rating is required (1.0 - 5.0)")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private Double communicationRating;

    @NotNull(message = "Productivity rating is required (1.0 - 5.0)")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private Double productivityRating;

    @NotNull(message = "Leadership rating is required (1.0 - 5.0)")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private Double leadershipRating;

    private String keyAchievements;
    private String areasOfImprovement;
    private String managerFeedback;
}
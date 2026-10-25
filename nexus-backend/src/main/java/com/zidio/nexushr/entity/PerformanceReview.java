package com.zidio.nexushr.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "performance_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The Employee Being Reviewed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // The Manager/HR conducting the review
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Employee reviewer;

    @Column(nullable = false, length = 50)
    private String reviewPeriod; // e.g. "Q3 2026", "Annual 2026"

    // 4 Core Performance Pillars (Ratings 1.0 to 5.0)
    @Column(nullable = false)
    private Double technicalSkillsRating;

    @Column(nullable = false)
    private Double communicationRating;

    @Column(nullable = false)
    private Double productivityRating;

    @Column(nullable = false)
    private Double leadershipRating;

    // Calculated Overall Performance Average (1.0 to 5.0)
    @Column(nullable = false)
    private Double overallScore;

    @Column(length = 1000)
    private String keyAchievements;

    @Column(length = 1000)
    private String areasOfImprovement;

    @Column(length = 1000)
    private String managerFeedback;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.FINALIZED;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
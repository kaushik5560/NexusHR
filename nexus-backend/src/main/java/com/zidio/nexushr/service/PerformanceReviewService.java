package com.zidio.nexushr.service;

import com.zidio.nexushr.dto.PerformanceReviewRequest;
import com.zidio.nexushr.dto.PerformanceReviewResponse;
import com.zidio.nexushr.entity.Employee;
import com.zidio.nexushr.entity.PerformanceReview;
import com.zidio.nexushr.entity.ReviewStatus;
import com.zidio.nexushr.repository.EmployeeRepository;
import com.zidio.nexushr.repository.PerformanceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceReviewService {

    private final PerformanceReviewRepository reviewRepository;
    private final EmployeeRepository employeeRepository;

    // 1. Submit 360° Performance Review (Manager / HR Action)
    @Transactional
    public PerformanceReviewResponse submitReview(String reviewerEmail, PerformanceReviewRequest request) {
        Employee reviewer = employeeRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found with email: " + reviewerEmail));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + request.getEmployeeId()));

        // Self-Review Protection Guard
        if (reviewer.getId().equals(employee.getId())) {
            throw new IllegalArgumentException("Self-review is not allowed! Reviews must be conducted by Manager or HR.");
        }

        // Calculate Overall Score Average (1.0 to 5.0)
        double sum = request.getTechnicalSkillsRating() 
                   + request.getCommunicationRating() 
                   + request.getProductivityRating() 
                   + request.getLeadershipRating();
        double overallScore = Math.round((sum / 4.0) * 100.0) / 100.0;

        PerformanceReview review = PerformanceReview.builder()
                .employee(employee)
                .reviewer(reviewer)
                .reviewPeriod(request.getReviewPeriod())
                .technicalSkillsRating(request.getTechnicalSkillsRating())
                .communicationRating(request.getCommunicationRating())
                .productivityRating(request.getProductivityRating())
                .leadershipRating(request.getLeadershipRating())
                .overallScore(overallScore)
                .keyAchievements(request.getKeyAchievements())
                .areasOfImprovement(request.getAreasOfImprovement())
                .managerFeedback(request.getManagerFeedback())
                .status(ReviewStatus.FINALIZED)
                .build();

        PerformanceReview saved = reviewRepository.save(review);
        return PerformanceReviewResponse.fromEntity(saved);
    }

    // 2. Get Performance Scorecards for Logged-in Employee (Self-Service)
    @Transactional(readOnly = true)
    public List<PerformanceReviewResponse> getMyReviews(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + email));

        return reviewRepository.findByEmployeeOrderByCreatedAtDesc(employee).stream()
                .map(PerformanceReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 3. Get Reviews for Specific Employee (Manager / HR View)
    @Transactional(readOnly = true)
    public List<PerformanceReviewResponse> getReviewsForEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + employeeId));

        return reviewRepository.findByEmployeeOrderByCreatedAtDesc(employee).stream()
                .map(PerformanceReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 4. Get Company-wide Performance History (HR Analytics)
    @Transactional(readOnly = true)
    public List<PerformanceReviewResponse> getAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(PerformanceReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
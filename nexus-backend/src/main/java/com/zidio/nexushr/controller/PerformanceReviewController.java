package com.zidio.nexushr.controller;

import com.zidio.nexushr.dto.MessageResponse;
import com.zidio.nexushr.dto.PerformanceReviewRequest;
import com.zidio.nexushr.dto.PerformanceReviewResponse;
import com.zidio.nexushr.service.PerformanceReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class PerformanceReviewController {

    private final PerformanceReviewService reviewService;

    // 1. POST: /api/reviews - Manager / HR Submits 360° Review
    @PostMapping
    @PreAuthorize("hasRole('HR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> submitReview(
            Authentication authentication,
            @Valid @RequestBody PerformanceReviewRequest request) {
        try {
            String reviewerEmail = authentication.getName();
            PerformanceReviewResponse response = reviewService.submitReview(reviewerEmail, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // 2. GET: /api/reviews/my-reviews - Current Employee's Performance Scorecard
    @GetMapping("/my-reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PerformanceReviewResponse>> getMyReviews(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(reviewService.getMyReviews(email));
    }

    // 3. GET: /api/reviews/employee/{id} - Specific Employee's Performance History
    @GetMapping("/employee/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getReviewsForEmployee(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reviewService.getReviewsForEmployee(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // 4. GET: /api/reviews/all - All Reviews (HR Dashboard)
    @GetMapping("/all")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<List<PerformanceReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
}
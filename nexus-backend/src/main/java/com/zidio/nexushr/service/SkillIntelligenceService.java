package com.zidio.nexushr.service;

import com.zidio.nexushr.dto.SkillGapAnalysisResponse;
import com.zidio.nexushr.dto.SkillGapAnalysisResponse.CareerMilestone;
import com.zidio.nexushr.entity.Employee;
import com.zidio.nexushr.entity.PerformanceReview;
import com.zidio.nexushr.repository.EmployeeRepository;
import com.zidio.nexushr.repository.PerformanceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillIntelligenceService {

    private final EmployeeRepository employeeRepository;
    private final PerformanceReviewRepository reviewRepository;

    public SkillGapAnalysisResponse generateCareerPathwayForEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + email));
        return generateCareerPathway(employee.getId());
    }

    public SkillGapAnalysisResponse generateCareerPathway(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + employeeId));

        List<PerformanceReview> reviews = reviewRepository.findByEmployeeOrderByCreatedAtDesc(employee);
        double performanceScore = reviews.isEmpty() ? 4.0 : reviews.get(0).getOverallScore();

        String department = employee.getDepartment();
        String currentRole = employee.getDesignation();
        String targetRole;
        double readinessScore;
        List<String> verifiedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();
        List<CareerMilestone> learningPath = new ArrayList<>();

        if ("Engineering".equalsIgnoreCase(department)) {
            targetRole = "Principal Solutions Architect / Engineering Lead";
            readinessScore = Math.min(94.0, Math.round((performanceScore / 5.0 * 75.0 + 15.0) * 10.0) / 10.0);

            verifiedSkills.addAll(List.of(
                    "Java 21 & Spring Boot Core",
                    "RESTful Microservices & JPA Hibernate",
                    "React 19 & TypeScript Frontend",
                    "PostgreSQL 17 Relational Schema Design",
                    "JWT Argon2 Security Protocols"
            ));

            missingSkills.addAll(List.of(
                    "High-Throughput Distributed Caching (Redis / Hazelcast)",
                    "Container Orchestration & Cloud Native (Docker & Kubernetes)",
                    "Event-Driven Architecture (Apache Kafka / RabbitMQ)",
                    "System Design & Scalability Trade-offs"
            ));

            learningPath.add(CareerMilestone.builder()
                    .timeframe("Month 1 (Days 1 - 30)")
                    .title("Cloud Infrastructure & Containerization")
                    .focusArea("Docker, Multi-stage Builds, Container Security")
                    .recommendedAction("Containerize NexusHR backend & frontend into isolated Docker networks")
                    .build());

            learningPath.add(CareerMilestone.builder()
                    .timeframe("Month 2 (Days 31 - 60)")
                    .title("Distributed Systems & Caching")
                    .focusArea("Redis Caching Layer & Query Optimization")
                    .recommendedAction("Implement secondary caching for employee directory and attendance lookups")
                    .build());

            learningPath.add(CareerMilestone.builder()
                    .timeframe("Month 3 (Days 61 - 90)")
                    .title("Architectural Leadership & Mentorship")
                    .focusArea("High-Level System Design & Code Review Governance")
                    .recommendedAction("Lead architectural walkthroughs and author RFC design documentation")
                    .build());

        } else {
            // HR / Management Pathway
            targetRole = "VP of People Operations / Chief Talent Officer";
            readinessScore = 82.5;

            verifiedSkills.addAll(List.of(
                    "Workforce Lifecycle Management",
                    "Statutory Payroll Compliance & Tax (PF/TDS)",
                    "Executive 360-Degree Performance Calibration",
                    "Employee Relations & Conflict Resolution"
            ));

            missingSkills.addAll(List.of(
                    "Predictive People Analytics & Workforce Modeling",
                    "Executive Compensation Benchmarking",
                    "Cross-Border Talent Acquisition Strategy"
            ));

            learningPath.add(CareerMilestone.builder()
                    .timeframe("Month 1 (Days 1 - 30)")
                    .title("Predictive Workforce Analytics")
                    .focusArea("Attrition Modeling & Flight Risk Mitigation")
                    .recommendedAction("Leverage NexusAI predictive radar for retention interventions")
                    .build());

            learningPath.add(CareerMilestone.builder()
                    .timeframe("Month 2 (Days 31 - 60)")
                    .title("Strategic Total Rewards Design")
                    .focusArea("Market Equity & Executive Compensation")
                    .recommendedAction("Design tiered incentive structures for critical engineering roles")
                    .build());

            learningPath.add(CareerMilestone.builder()
                    .timeframe("Month 3 (Days 61 - 90)")
                    .title("Global Talent Strategy")
                    .focusArea("Organizational Scaling & Culture Retention")
                    .recommendedAction("Formulate comprehensive 2027 enterprise workforce growth roadmap")
                    .build());
        }

        String readinessLevel = readinessScore >= 75.0 ? "High Readiness (Target 60-90 Days)" :
                                readinessScore >= 50.0 ? "Developing (Target 6-12 Months)" : "Early Stage";

        String advice = String.format(
                "Based on current delivery rating (%.1f/5.0), employee demonstrates %s for promotion to %s. Recommended priority: Focus on %s.",
                performanceScore,
                readinessLevel.toLowerCase(),
                targetRole,
                missingSkills.get(0)
        );

        return SkillGapAnalysisResponse.builder()
                .employeeId(employee.getId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .currentRole(currentRole)
                .targetRole(targetRole)
                .readinessScore(readinessScore)
                .readinessLevel(readinessLevel)
                .verifiedSkills(verifiedSkills)
                .identifiedSkillGaps(missingSkills)
                .learningPath(learningPath)
                .aiCareerAdvice(advice)
                .build();
    }
}

package com.zidio.nexushr.controller;

import com.zidio.nexushr.dto.AttritionRiskResponse;
import com.zidio.nexushr.dto.MessageResponse;
import com.zidio.nexushr.dto.SkillGapAnalysisResponse;
import com.zidio.nexushr.dto.WorkforceRiskOverviewResponse;
import com.zidio.nexushr.service.SkillIntelligenceService;
import com.zidio.nexushr.service.WorkforceAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AiIntelligenceController {

    private final WorkforceAiService workforceAiService;
    private final SkillIntelligenceService skillIntelligenceService;

    // 1. GET: /api/ai/attrition/employee/{id} - Single employee flight risk
    @GetMapping("/attrition/employee/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getEmployeeAttritionRisk(@PathVariable Long id) {
        try {
            AttritionRiskResponse response = workforceAiService.analyzeEmployeeAttritionRisk(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // 2. GET: /api/ai/attrition/overview - Company-wide AI risk matrix
    @GetMapping("/attrition/overview")
    @PreAuthorize("hasRole('HR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<WorkforceRiskOverviewResponse> getWorkforceOverview() {
        return ResponseEntity.ok(workforceAiService.getCompanyWorkforceRiskOverview());
    }

    // 3. GET: /api/ai/skills/my-pathway - Logged-in employee's AI Career Pathway & Skill Gap
    @GetMapping("/skills/my-pathway")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyCareerPathway(Authentication authentication) {
        try {
            String email = authentication.getName();
            SkillGapAnalysisResponse response = skillIntelligenceService.generateCareerPathwayForEmail(email);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // 4. GET: /api/ai/skills/employee/{id} - Specific employee career pathway
    @GetMapping("/skills/employee/{id}")
    @PreAuthorize("hasRole('HR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getEmployeeCareerPathway(@PathVariable Long id) {
        try {
            SkillGapAnalysisResponse response = skillIntelligenceService.generateCareerPathway(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}

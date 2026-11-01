package com.zidio.nexushr.controller;

import com.zidio.nexushr.dto.BroadcastRequest;
import com.zidio.nexushr.dto.DispatchLogResponse;
import com.zidio.nexushr.service.CommunicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communications")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class CommunicationController {

    private final CommunicationService communicationService;

    // 1. POST: /api/communications/broadcast - Dispatch announcement via Email / SMS
    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('HR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<DispatchLogResponse>> broadcastAnnouncement(@Valid @RequestBody BroadcastRequest request) {
        List<DispatchLogResponse> responses = communicationService.dispatchBroadcast(request);
        return ResponseEntity.ok(responses);
    }

    // 2. GET: /api/communications/audit-logs - Live delivery ledger
    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('HR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<DispatchLogResponse>> getAuditLogs() {
        return ResponseEntity.ok(communicationService.getDispatchLogs());
    }
}

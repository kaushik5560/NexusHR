package com.zidio.nexushr.service;

import com.zidio.nexushr.dto.BroadcastRequest;
import com.zidio.nexushr.dto.DispatchLogResponse;
import com.zidio.nexushr.entity.DispatchLog;
import com.zidio.nexushr.entity.Employee;
import com.zidio.nexushr.repository.DispatchLogRepository;
import com.zidio.nexushr.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunicationService {

    private static final Logger logger = LoggerFactory.getLogger(CommunicationService.class);
    private final DispatchLogRepository dispatchLogRepository;
    private final EmployeeRepository employeeRepository;
    private final Random random = new Random();

    // 1. Dispatch multi-channel broadcast to company workforce
    @Transactional
    public List<DispatchLogResponse> dispatchBroadcast(BroadcastRequest request) {
        List<Employee> targets = employeeRepository.findAll();
        List<DispatchLog> logsToSave = new ArrayList<>();
        List<String> channels = (request.getChannels() == null || request.getChannels().isEmpty()) ?
                List.of("EMAIL") : request.getChannels();

        for (Employee emp : targets) {
            // Check department audience filter
            if (!"ALL".equalsIgnoreCase(request.getTargetAudience()) &&
                    request.getTargetAudience() != null &&
                    !request.getTargetAudience().equalsIgnoreCase(emp.getDepartment())) {
                continue;
            }

            // A. Email Dispatch
            if (channels.contains("EMAIL") && emp.getEmail() != null) {
                long latency = 25 + random.nextInt(35); // 25-60 ms simulated SMTP handshake
                DispatchLog emailLog = DispatchLog.builder()
                        .channel("EMAIL")
                        .recipient(emp.getEmail())
                        .subject(request.getSubject())
                        .content(request.getContent())
                        .deliveryStatus("DELIVERED")
                        .latencyMs(latency)
                        .build();
                logsToSave.add(emailLog);
                logger.info("[SMTP MOCK] Dispatched email to {} (Subject: {}) in {}ms", emp.getEmail(), request.getSubject(), latency);
            }

            // B. SMS Dispatch
            if (channels.contains("SMS")) {
                String phone = (emp.getPhoneNumber() != null && !emp.getPhoneNumber().isEmpty()) ?
                        emp.getPhoneNumber() : "+91-9876543210";
                long latency = 40 + random.nextInt(50); // 40-90 ms simulated telecom carrier gateway
                DispatchLog smsLog = DispatchLog.builder()
                        .channel("SMS")
                        .recipient(phone)
                        .subject(request.getSubject())
                        .content(request.getContent())
                        .deliveryStatus("DELIVERED")
                        .latencyMs(latency)
                        .build();
                logsToSave.add(smsLog);
                logger.info("[SMS GATEWAY MOCK] Dispatched SMS to {} in {}ms", phone, latency);
            }
        }

        List<DispatchLog> saved = dispatchLogRepository.saveAll(logsToSave);
        return saved.stream().map(DispatchLogResponse::fromEntity).collect(Collectors.toList());
    }

    // 2. Fetch live dispatch audit trail
    @Transactional(readOnly = true)
    public List<DispatchLogResponse> getDispatchLogs() {
        return dispatchLogRepository.findAllByOrderByDispatchedAtDesc()
                .stream()
                .map(DispatchLogResponse::fromEntity)
                .collect(Collectors.toList());
    }
}

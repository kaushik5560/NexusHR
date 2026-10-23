package com.zidio.nexushr.service;

import com.zidio.nexushr.dto.LeaveActionRequest;
import com.zidio.nexushr.dto.LeaveApplyRequest;
import com.zidio.nexushr.dto.LeaveResponse;
import com.zidio.nexushr.entity.Employee;
import com.zidio.nexushr.entity.LeaveRequest;
import com.zidio.nexushr.entity.LeaveStatus;
import com.zidio.nexushr.repository.EmployeeRepository;
import com.zidio.nexushr.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    // 1. Employee Applies for Leave
    @Transactional
    public LeaveResponse applyLeave(String email, LeaveApplyRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date!");
        }

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + email));

        // Calculate total days inclusive (e.g. 24th to 26th = 3 days)
        int totalDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(employee)
                .leaveType(request.getLeaveType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalDays(totalDays)
                .reason(request.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        LeaveRequest saved = leaveRepository.save(leaveRequest);
        return LeaveResponse.fromEntity(saved);
    }

    // 2. Get All Leaves for Current Employee
    @Transactional(readOnly = true)
    public List<LeaveResponse> getMyLeaves(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + email));

        return leaveRepository.findByEmployeeOrderByAppliedAtDesc(employee).stream()
                .map(LeaveResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 3. Get All Pending Leaves (For HR / Manager Approval Queue)
    @Transactional(readOnly = true)
    public List<LeaveResponse> getPendingLeaves() {
        return leaveRepository.findByStatusOrderByAppliedAtDesc(LeaveStatus.PENDING).stream()
                .map(LeaveResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 4. Get All Leaves (Company-wide history)
    @Transactional(readOnly = true)
    public List<LeaveResponse> getAllLeaves() {
        return leaveRepository.findAllByOrderByAppliedAtDesc().stream()
                .map(LeaveResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 5. Manager / HR Process Leave (Approve or Reject)
    @Transactional
    public LeaveResponse processLeave(Long leaveId, String reviewerEmail, LeaveActionRequest action) {
        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found with id: " + leaveId));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalArgumentException("This leave request has already been processed as: " + leave.getStatus());
        }

        Employee reviewer = employeeRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found with email: " + reviewerEmail));

        leave.setStatus(action.getStatus());
        leave.setManagerRemarks(action.getRemarks());
        leave.setReviewedBy(reviewer);
        leave.setReviewedAt(LocalDateTime.now());

        LeaveRequest updated = leaveRepository.save(leave);
        return LeaveResponse.fromEntity(updated);
    }
}
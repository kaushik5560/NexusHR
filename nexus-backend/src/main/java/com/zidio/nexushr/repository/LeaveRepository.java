package com.zidio.nexushr.repository;

import com.zidio.nexushr.entity.Employee;
import com.zidio.nexushr.entity.LeaveRequest;
import com.zidio.nexushr.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeOrderByAppliedAtDesc(Employee employee);
    List<LeaveRequest> findByStatusOrderByAppliedAtDesc(LeaveStatus status);
    List<LeaveRequest> findAllByOrderByAppliedAtDesc();
}
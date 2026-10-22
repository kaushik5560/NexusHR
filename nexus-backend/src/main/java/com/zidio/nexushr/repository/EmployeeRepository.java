package com.zidio.nexushr.repository;

import com.zidio.nexushr.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByEmployeeCode(String employeeCode);
    Boolean existsByEmail(String email);
    Boolean existsByEmployeeCode(String employeeCode);
}
package com.zidio.nexushr.repository;

import com.zidio.nexushr.entity.Employee;
import com.zidio.nexushr.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findByEmployeeOrderByYearDescMonthDesc(Employee employee);
    Optional<Payroll> findByEmployeeAndMonthAndYear(Employee employee, int month, int year);
    List<Payroll> findByMonthAndYear(int month, int year);
    Boolean existsByEmployeeAndMonthAndYear(Employee employee, int month, int year);
}
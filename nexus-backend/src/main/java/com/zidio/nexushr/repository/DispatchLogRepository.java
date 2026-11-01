package com.zidio.nexushr.repository;

import com.zidio.nexushr.entity.DispatchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispatchLogRepository extends JpaRepository<DispatchLog, Long> {
    List<DispatchLog> findAllByOrderByDispatchedAtDesc();
}

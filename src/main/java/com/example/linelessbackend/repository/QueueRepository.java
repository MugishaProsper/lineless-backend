package com.example.linelessbackend.repository;

import com.example.linelessbackend.model.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {
    List<Queue> findByIsActiveTrue();
    List<Queue> findByAdminId(Long adminId);
    List<Queue> findByCompanyId(Long companyId);
} 
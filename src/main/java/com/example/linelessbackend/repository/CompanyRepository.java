package com.example.linelessbackend.repository;

import com.example.linelessbackend.model.Company;
import com.example.linelessbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByCode(String code);
    List<Company> findByAdminsContaining(User admin);
} 
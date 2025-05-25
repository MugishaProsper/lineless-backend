package com.example.linelessbackend.controller;

import com.example.linelessbackend.dto.CompanyDTO;
import com.example.linelessbackend.dto.CompanyStatisticsDTO;
import com.example.linelessbackend.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);

    @Autowired
    private CompanyService companyService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CompanyDTO> createCompany(@RequestBody CompanyDTO companyDTO) {
        logger.info("Creating new company: {}", companyDTO.getName());
        return ResponseEntity.ok(companyService.createCompany(companyDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<CompanyDTO> updateCompany(@PathVariable Long id, @RequestBody CompanyDTO companyDTO) {
        logger.info("Updating company with id: {}", id);
        return ResponseEntity.ok(companyService.updateCompany(id, companyDTO));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    public ResponseEntity<CompanyDTO> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompany(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        logger.info("Deleting company with id: {}", id);
        companyService.deleteCompany(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<CompanyStatisticsDTO> getCompanyStatistics(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompanyStatistics(id));
    }

    @PutMapping("/{id}/settings")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<CompanyDTO> updateCompanySettings(@PathVariable Long id, @RequestBody CompanyDTO companyDTO) {
        logger.info("Updating settings for company with id: {}", id);
        return ResponseEntity.ok(companyService.updateCompanySettings(id, companyDTO));
    }

    @PutMapping("/{id}/admin/{adminId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CompanyDTO> assignAdmin(@PathVariable Long id, @PathVariable Long adminId) {
        logger.info("Assigning admin {} to company {}", adminId, id);
        return ResponseEntity.ok(companyService.assignAdmin(id, adminId));
    }
} 